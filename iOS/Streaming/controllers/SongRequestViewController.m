//
//  SongRequestViewControllerViewController.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SongRequestViewController.h"

#import "../operations/RequestSongOperation.h"
#import "../models/SongRequest.h"
#import "../enums/Error.h"

@interface SongRequestViewController ()

@end

@implementation SongRequestViewController

@synthesize contentView;
@synthesize scrollView;

@synthesize emailTextView;
@synthesize autoreTextView;
@synthesize titoloTextView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"CONTROLLER_TITLE_SONGREQUEST", @"");
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

	if(self.scrollView != nil && self.contentView != nil) {
		NSLog(@"adding fields to scrollview");
		[self.scrollView addSubview: self.contentView];
		self.scrollView.contentSize = self.contentView.bounds.size;
	}
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

	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(receiveNotification:) name:@"SendEmail" object:nil];

	[self clearForm];

	NSString *filePath =[self getDataFilePath];
	if([[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
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

- (BOOL) textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (void) receiveNotification: (NSNotification *)notification
{
	if([[notification name] isEqualToString:@"SendEmail"]) {
		[self receiveSendEmailNotification:notification];
	}
}

- (void) receiveSendEmailNotification:(NSNotification *)notification
{
	NSLog(@"called receiveSendEmailNotification");

	[self performSelectorOnMainThread:@selector(sendEmailCompleted:) withObject:[notification object] waitUntilDone:YES];
}

- (void) showLoadingDialog
{
	if(dialog != nil) {
		[dialog release];
	}

	NSString *dialogTitle = NSLocalizedString(@"DIALOG_LOADING", @"");
	dialog = [[LoadingDialog alloc] initWithTitle:dialogTitle message:nil delegate:nil cancelButtonTitle:nil otherButtonTitles:nil];
	[dialog show];
}

- (IBAction) sendEmail:(id)sender
{
	if([[titoloTextView text] length] == 0 ||
	   [[autoreTextView text] length] == 0 ||
	   [[emailTextView text] length] == 0) {
		NSLog(@"Something is missing");
		NSString *title = NSLocalizedString(@"DIALOG_SOMETHING_MISSING_TITLE", @"");
		NSString *message = NSLocalizedString(@"DIALOG_SOMETHING_MISSING_MESSAGE", @"");
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] autorelease];
		[alert show];
	} else {
		NSLog(@"OK!");

		[timer invalidate];
		[self saveEmailAddress];

		SongRequest *request = [[SongRequest alloc] init];
		request.title = titoloTextView.text;
		request.author = autoreTextView.text;
		request.email = emailTextView.text;

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
		NSString *title = NSLocalizedString(@"DIALOG_SEND_FAILED_TITLE", @"");
		NSString *message = NSLocalizedString(@"DIALOG_SEND_FAILED_MESSAGE", @"");
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] autorelease];
		[alert show];
		return;
	}

	NSNumber *errorCode = [serverResponse objectForKey:@"errorCode"];
	NSLog(@"errorCode: %d", [errorCode integerValue]);
	BOOL containsError = ([errorCode intValue] != NO_ERROR);
	NSLog(@"contains error: %@", containsError ? @"YES" : @"NO");
	if(!containsError) {
		NSString *title = NSLocalizedString(@"DIALOG_SEND_OK_TITLE", @"");
		NSString *message = NSLocalizedString(@"DIALOG_SEND_OK_TITLE_MESSAGE", @"");
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] autorelease];
		[alert show];
		[self clearForm];
	} else {
		NSString *title = NSLocalizedString(@"DIALOG_SEND_FAILED_TITLE", @"");
		NSString *message = NSLocalizedString(@"DIALOG_SEND_FAILED_MESSAGE", @"");
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] autorelease];
		[alert show];
	}
}

- (void) clearForm
{
	self.titoloTextView.text = @"";
	self.autoreTextView.text = @"";
}

/*- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    [self animateTextField: textField up: YES];
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    [self animateTextField: textField up: NO];
}*/

- (void) animateTextField: (UITextField*) textField up: (BOOL) up
{
    const int movementDistance = 50; // tweak as needed
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
