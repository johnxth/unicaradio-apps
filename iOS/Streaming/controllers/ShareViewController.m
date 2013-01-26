//
//  ShareViewController.m
//  Streaming
//
//  Created by Paolo on 25/01/13.
//
//

#import "ShareViewController.h"

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
    }
    return self;
}

+ (void) shareOnFacebook:(UIViewController *)viewController
{
	SLComposeViewController *controller = [SLComposeViewController composeViewControllerForServiceType:SLServiceTypeFacebook];
	[controller setInitialText:@"Test Post from mobile.safilsunny.com"];
	[controller addURL:[NSURL URLWithString:@"http://www.mobile.safilsunny.com"]];

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
        [controller setInitialText:@"Test Post from mobile.safilsunny.com"];
        [controller addURL:[NSURL URLWithString:@"http://www.mobile.safilsunny.com"]];

        [viewController presentViewController:controller animated:YES completion:Nil];
	} else {
		TWTweetComposeViewController *tweetSheet = [[TWTweetComposeViewController alloc] init];
		[tweetSheet setInitialText:@"Just learned how to use the #iOS5 Twitter Framework on @buildinternet"];
		[tweetSheet addURL:[NSURL URLWithString:@"http://buildinternet.com/2011/10/ios-creating-your-own-tweet-sheet"]];

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

	[mailer setMessageBody:@"Test" isHTML:NO];

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

@end
