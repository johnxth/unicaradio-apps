//
//  SongRequestViewControllerViewController.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SongRequestViewController.h"

#import "../operations/DownloadCaptchaOperation.h"
#import "../utils/CaptchaParser.h"

@interface SongRequestViewController ()

@end

@implementation SongRequestViewController

@synthesize contentView;
@synthesize scrollView;
@synthesize captchaTextView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"Request song", @"Request song");
        self.tabBarItem.image = [UIImage imageNamed:@"song"];
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
	NSLog(@"SongRequestViewController - viewDidAppear");

	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(receiveGetCaptchaNotification:) name:@"GetCaptcha" object:nil];

	NSOperationQueue *queue = [NSOperationQueue new];
	DownloadCaptchaOperation *operation = [[DownloadCaptchaOperation alloc] init];
	[queue addOperation:operation];
	[operation release];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

- (BOOL) textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

- (void) receiveGetCaptchaNotification:(NSNotification *) notification
{
	NSLog(@"called receiveGetCaptchaNotification");
	NSString *captcha = [notification object];
	NSLog(@"got captcha");

	NSString *parsedCaptcha = [CaptchaParser parse:captcha];
	[captchaTextView setPlaceholder:parsedCaptcha];
}

- (void) dealloc
{
	[super dealloc];
	[[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
