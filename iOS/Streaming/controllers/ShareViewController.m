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
		}
    }
    return self;
}

+ (void) shareOnFacebook:(UIViewController *)viewController
{
	SLComposeViewController *controller = [SLComposeViewController composeViewControllerForServiceType:SLServiceTypeFacebook];
	[controller setInitialText:[ShareViewController getSharingMessage]];
	[controller addURL:[NSURL URLWithString:@"http://www.unicaradio.it"]];

	[viewController presentViewController:controller animated:YES completion:nil];
}

- (IBAction) shareOnFacebook:(id)sender
{
	[ShareViewController shareOnFacebook:self];
	[popoverController dismissPopoverAnimated:YES];
}

+ (void) shareOnTwitter:(UIViewController *)viewController
{
	if(NSClassFromString(@"SLComposeViewController") != nil) {
		SLComposeViewController *controller = [SLComposeViewController composeViewControllerForServiceType:SLServiceTypeTwitter];
        [controller setInitialText:[ShareViewController getSharingMessage]];

        [viewController presentViewController:controller animated:YES completion:Nil];
	} else {
		TWTweetComposeViewController *tweetSheet = [[TWTweetComposeViewController alloc] init];
		[tweetSheet setInitialText:[ShareViewController getSharingMessage]];

		[viewController presentModalViewController:tweetSheet animated:YES];
	}
}

- (IBAction) shareOnTwitter:(id)sender
{
	[ShareViewController shareOnTwitter:self];
	[popoverController dismissPopoverAnimated:YES];
}

+ (void) shareOnEmail:(UIViewController *)viewController
{
	MFMailComposeViewController *mailer = [[MFMailComposeViewController alloc] init];
	mailer.mailComposeDelegate = viewController;

	[mailer setMessageBody:[ShareViewController getSharingMessage] isHTML:NO];
	[mailer setSubject:NSLocalizedString(@"SUGGEST_SUBJECT", @"")];

	if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
		mailer.modalPresentationStyle = UIModalPresentationPageSheet;
	}

	[viewController presentModalViewController:mailer animated:YES];
}

- (IBAction) shareOnEmail:(id)sender
{
	[ShareViewController shareOnEmail:self];
	[popoverController dismissPopoverAnimated:YES];
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

+ (void)share:(SharingPlace)place withUIViewController:(UIViewController *)viewController
{
	switch (place) {
		case TWITTER:
			[self shareOnTwitter:viewController];
			break;
		case EMAIL:
			[self shareOnEmail:viewController];
			break;
		case FACEBOOK:
			[self shareOnFacebook:viewController];
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
