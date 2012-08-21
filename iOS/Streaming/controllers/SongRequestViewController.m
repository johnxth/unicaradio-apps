//
//  SongRequestViewControllerViewController.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SongRequestViewController.h"

#import "../operations/DownloadCaptchaOperation.h"
#import "../operations/RequestSongOperation.h"
#import "../utils/CaptchaParser.h"
#import "../models/SongRequest.h"
#import "../enums/Error.h"

@interface SongRequestViewController ()

@end

@implementation SongRequestViewController

@synthesize captcha;

@synthesize contentView;
@synthesize scrollView;

@synthesize emailTextView;
@synthesize autoreTextView;
@synthesize titoloTextView;
@synthesize captchaTextView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"Request song", @"Request song");
        self.tabBarItem.image = [UIImage imageNamed:@"song"];
		queue = [[NSOperationQueue alloc] init];
		[queue setMaxConcurrentOperationCount: 1];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	NSLog(@"SongRequestViewController - viewDidLoad");
	// Do any additional setup after loading the view, typically from a nib.
	
	[self.scrollView addSubview: self.contentView];
    self.scrollView.contentSize = self.contentView.bounds.size;
}

- (void)viewDidUnload
{
	self.scrollView  = nil;
    self.contentView = nil;

    [super viewDidUnload];
}

- (void) viewDidAppear:(BOOL)animated
{
	[super viewDidAppear:animated];
	NSLog(@"SongRequestViewController - viewDidAppear");

	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(receiveNotification:) name:@"GetCaptcha" object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(receiveNotification:) name:@"SendEmail" object:nil];

	[self clearForm];

	NSString *filePath =[self getDataFilePath];
	if([[NSFileManager defaultManager] fileExistsAtPath:filePath])
	{
		NSDictionary *dict = [[NSDictionary alloc] initWithContentsOfFile:filePath];
		emailTextView.text = [dict objectForKey:@"email"];
		[dict release];
	}
}

- (void) viewDidDisappear:(BOOL)animated
{
	[[NSNotificationCenter defaultCenter] removeObserver:self];
	[timer invalidate];
	timer = nil;

	[super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

- (BOOL) textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

- (void) receiveNotification: (NSNotification *)notification
{
	if([[notification name] isEqualToString:@"GetCaptcha"]) {
		[self receiveGetCaptchaNotification:notification];
	} else if([[notification name] isEqualToString:@"SendEmail"]) {
		[self receiveSendEmailNotification:notification];
	}
}

- (void) receiveGetCaptchaNotification:(NSNotification *)notification
{
	NSLog(@"called receiveGetCaptchaNotification");

	[self performSelectorOnMainThread:@selector(displayCaptcha:) withObject:[notification object] waitUntilDone:YES];
}

- (void) receiveSendEmailNotification:(NSNotification *)notification
{
	NSLog(@"called receiveGetCaptchaNotification");

	[self performSelectorOnMainThread:@selector(sendEmailCompleted:) withObject:[notification object] waitUntilDone:YES];
}

- (void) showLoadingDialog
{
	if(dialog != nil) {
		[dialog release];
	}

	dialog = [[LoadingDialog alloc] initWithTitle:@"Loading..." message:nil delegate:nil cancelButtonTitle:nil otherButtonTitles:nil];
	[dialog show];
}

- (void) loadCaptcha
{
	[self showLoadingDialog];

	DownloadCaptchaOperation *operation = [[DownloadCaptchaOperation alloc] init];
	[queue addOperation:operation];
	[operation release];
}

- (void) displayCaptcha:(NSDictionary *)captchaResponse
{
	captchaTextView.text = @"";
	if(captchaResponse == nil) {
		[timer invalidate];
		captchaTextView.placeholder = @"Non sei connesso ad Internet";
		return;
	}

	NSNumber *errorCode = [captchaResponse objectForKey:@"errorCode"];
	NSLog(@"errorCode: %d", [errorCode integerValue]);
	BOOL containsError = ([errorCode intValue] != NO_ERROR);
	NSLog(@"captchaResponse contains error: %@", containsError ? @"YES" : @"NO");
	if(containsError) {
		[timer invalidate];
		captchaTextView.placeholder = @"Impossibile ottenere un captcha";
		return;
	}

	NSString *captchaFromServer = [captchaResponse objectForKey:@"result"];
	NSLog(@"Captcha: %@", captchaFromServer);

	self.captcha = [NSString stringWithString:captchaFromServer];
	NSLog(@"got captcha: %@", self.captcha);
	parsedCaptcha = [CaptchaParser parse:self.captcha];
	NSLog(@"captcha parsed");

	captchaTextView.placeholder = parsedCaptcha;
	NSLog(@"captcha displayed");
	[dialog dismiss];

	timer = [NSTimer scheduledTimerWithTimeInterval:4 * 60 target:self selector:@selector(loadCaptcha) userInfo:nil repeats:NO];
}

- (IBAction) sendEmail:(id)sender
{
	if([[titoloTextView text] length] == 0 ||
	   [[autoreTextView text] length] == 0 ||
	   [[captchaTextView text] length] == 0 ||
	   [[emailTextView text] length] == 0) {
		NSLog(@"Something is missing");
		NSString *title = @"Something is missing";
		NSString *message = @"Hey! You forgot something!";
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] autorelease];
		[alert show];
	} else {
		NSLog(@"OK!");
		NSLog(@"captcha: %@", self.captcha);

		[timer invalidate];
		[self saveEmailAddress];

		SongRequest *request = [[SongRequest alloc] init];
		request.title = titoloTextView.text;
		request.author = autoreTextView.text;
		request.captcha = self.captcha;
		request.email = emailTextView.text;
		request.result = captchaTextView.text;

		[self showLoadingDialog];
		RequestSongOperation *operation = [[RequestSongOperation alloc] initWithSongRequest:request];
		[queue addOperation:operation];
	}
}

- (void) saveEmailAddress
{
	NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
	[dict setObject:emailTextView.text forKey:@"email"];
	[dict writeToFile:[self getDataFilePath] atomically:YES];
	[dict release];
}

- (void) sendEmailCompleted:(NSDictionary *)serverResponse
{
	[dialog dismiss];
	if(serverResponse == nil) {
		NSString *title = @"Invio fallito";
		NSString *message = @"Non è stato possibile inviare la richiesta.";
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] autorelease];
		[alert show];
		return;
	}

	NSNumber *errorCode = [serverResponse objectForKey:@"errorCode"];
	NSLog(@"errorCode: %d", [errorCode integerValue]);
	BOOL containsError = ([errorCode intValue] != NO_ERROR);
	NSLog(@"contains error: %@", containsError ? @"YES" : @"NO");
	if(!containsError) {
		NSString *title = @"Richiesta inviata";
		NSString *message = @"La richiesta è stata ricevuta.";
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] autorelease];
		[alert show];
		[self clearForm];
	} else {
		NSString *title = @"Invio fallito";
		NSString *message = @"Non è stato possibile inviare la richiesta.";
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] autorelease];
		[alert show];
		[self loadCaptcha];
	}
}

- (void) clearForm
{
	self.titoloTextView.text = @"";
	self.autoreTextView.text = @"";
	self.captchaTextView.text = @"";
	[self loadCaptcha];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    [self animateTextField: textField up: YES];
}


- (void)textFieldDidEndEditing:(UITextField *)textField
{
    [self animateTextField: textField up: NO];
}

- (void) animateTextField: (UITextField*) textField up: (BOOL) up
{
    const int movementDistance = 80; // tweak as needed
    const float movementDuration = 0.3f; // tweak as needed
	
    int movement = (up ? -movementDistance : movementDistance);
	
    [UIView beginAnimations: @"anim" context: nil];
    [UIView setAnimationBeginsFromCurrentState: YES];
    [UIView setAnimationDuration: movementDuration];
    self.view.frame = CGRectOffset(self.view.frame, 0, movement);
    [UIView commitAnimations];
}

- (NSString *) getDataFilePath
{
	NSArray *path = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [path objectAtIndex:0];
	return [documentsDirectory stringByAppendingPathComponent:kPlistname];
}

- (void) dealloc
{
	[super dealloc];
	[[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
