//
//  ShareViewController.m
//  Streaming
//
//  Created by Paolo on 25/01/13.
//
//

#import "ShareViewController.h"
#import "AppDelegate.h"
#import "StreamingViewController.h"
#import "UnicaradioUINavigationController.h"
#import "SystemUtils.h"

#import <Twitter/Twitter.h>
#import <Social/Social.h>
#import <Accounts/Accounts.h>

@interface ShareViewController ()

@end

@implementation ShareViewController

@synthesize delegate;

- (id)init
{
	NSString *nibName = @"ShareView_iOS5";
	if(NSClassFromString(@"SLComposeViewController") != nil) {
		nibName = @"ShareView_iOS6";
	}
    self = [super initWithNibName:nibName bundle:nil];
    if (self) {
		self.title = NSLocalizedString(@"SHARE_SHEET_TITLE", @"");
		
		if(SYSTEM_VERSION_LESS_THAN(@"7.0")) {
			[self.view setBackgroundColor:[UIColor blackColor]];
			self.twitterLabel.textColor = [UIColor whiteColor];
			self.facebookLabel.textColor = [UIColor whiteColor];
			self.mailLabel.textColor = [UIColor whiteColor];
		}
    }
    return self;
}

+ (UIViewController *) shareOnFacebook
{
	SLComposeViewController *controller = [SLComposeViewController composeViewControllerForServiceType:SLServiceTypeFacebook];
	[controller setInitialText:[ShareViewController getSharingMessage]];
	[controller addURL:[NSURL URLWithString:@"http://www.unicaradio.it"]];

	//[viewController presentViewController:controller animated:YES completion:nil];
	return controller;
}

- (IBAction) shareOnFacebook:(id)sender
{
	[self.delegate presentViewControllerForSharePopover:[ShareViewController shareOnFacebook]];
	[self.delegate dismissSharePopoverAnimated:YES];
	//[popoverController dismissPopoverAnimated:YES];
}

+ (UIViewController *) shareOnTwitter
{
	if(NSClassFromString(@"SLComposeViewController") != nil) {
		SLComposeViewController *controller = [SLComposeViewController composeViewControllerForServiceType:SLServiceTypeTwitter];
        [controller setInitialText:[ShareViewController getSharingMessage]];

		//[viewController presentViewController:controller animated:YES completion:nil];
		return controller;
	} else {
		TWTweetComposeViewController *tweetSheet = [[TWTweetComposeViewController alloc] init];
		[tweetSheet setInitialText:[ShareViewController getSharingMessage]];

		//[viewController presentModalViewController:tweetSheet animated:YES];
		return tweetSheet;
	}
}

- (IBAction) shareOnTwitter:(id)sender
{
	[self.delegate presentViewControllerForSharePopover:[ShareViewController shareOnTwitter]];
	[self.delegate dismissSharePopoverAnimated:YES];
	//[popoverController dismissPopoverAnimated:YES];
}

+ (UIViewController *) shareOnEmail:(UIViewController *)viewController
{
	MFMailComposeViewController *mailer = [[MFMailComposeViewController alloc] init];
	mailer.mailComposeDelegate = viewController;

	[mailer setMessageBody:[ShareViewController getSharingMessage] isHTML:NO];
	[mailer setSubject:NSLocalizedString(@"SUGGEST_SUBJECT", @"")];

	if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
		mailer.modalPresentationStyle = UIModalPresentationPageSheet;
	}

	//[viewController presentModalViewController:mailer animated:YES];
	return mailer;
}

- (IBAction) shareOnEmail:(id)sender
{
	UIViewController *viewController = (UIViewController *) self.delegate;
	[self.delegate presentViewControllerForSharePopover:[ShareViewController shareOnEmail:viewController]];
	[self.delegate dismissSharePopoverAnimated:YES];
	//[popoverController dismissPopoverAnimated:YES];
}

- (void)mailComposeController:(MFMailComposeViewController*)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError*)error
{
    [self dismissModalViewControllerAnimated:YES];
}

- (void) setPopOver:(UIPopoverController *)_popover
{
	popoverController = _popover;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

+ (UIViewController *) share:(SharingPlace)place withUIViewController:(UIViewController *)viewController
{
	switch (place) {
		case TWITTER:
			return [self shareOnTwitter];
			break;
		case EMAIL:
			return [self shareOnEmail:viewController];
			break;
		case FACEBOOK:
			return [self shareOnFacebook];
		default:
			break;
	}
}

+ (NSString *) getSharingMessage
{
	AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
	UnicaradioUINavigationController *controller = (UnicaradioUINavigationController *) [appDelegate.tabBarController.viewControllers objectAtIndex:0];
	StreamingViewController *streamingController = (StreamingViewController *) [controller topViewController];

	TrackInfos *infos = streamingController.infos;
	if(infos == nil || [infos isClean]) {
		return NSLocalizedString(@"SUGGEST_CONTENT", @"");
	} else {
		if(infos.title == nil || [infos.title isEqualToString:@""]) {
			return [NSString stringWithFormat:NSLocalizedString(@"SUGGEST_CONTENT_ONLY_AUTHOR", @""), infos.author, nil];
		} else {
			return [NSString stringWithFormat:NSLocalizedString(@"SUGGEST_CONTENT_SONG", @""), infos.title, infos.author, nil];
		}
	}
}

@end
