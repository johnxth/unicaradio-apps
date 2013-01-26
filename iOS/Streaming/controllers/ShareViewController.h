//
//  ShareViewController.h
//  Streaming
//
//  Created by Paolo on 25/01/13.
//
//

#import <UIKit/UIKit.h>

#import <MessageUI/MessageUI.h>

@interface ShareViewController : UIViewController<MFMailComposeViewControllerDelegate>
{
	UIPopoverController *popoverController;
}

typedef enum {
	TWITTER,
	EMAIL,
	FACEBOOK
} SharingPlace;

- (void) setPopOver:(UIPopoverController *)popover;

- (IBAction) shareOnFacebook:(id)sender;
- (IBAction) shareOnTwitter:(id)sender;
- (IBAction) shareOnEmail:(id)sender;

+ (void) share: (SharingPlace) place withUIViewController:(UIViewController *)viewController;

@end
