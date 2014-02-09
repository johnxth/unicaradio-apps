//
//  ShareViewController.h
//  Streaming
//
//  Created by Paolo on 25/01/13.
//
//

#import <UIKit/UIKit.h>

#import <MessageUI/MessageUI.h>

@protocol SharePopoverDelegate <NSObject>

- (void) dismissSharePopoverAnimated:(BOOL)animated;
- (void) presentViewControllerForSharePopover:(UIViewController *)viewController;

@end

@interface ShareViewController : UIViewController<MFMailComposeViewControllerDelegate>
{
	UIPopoverController *popoverController;
}

@property (nonatomic, retain) id<SharePopoverDelegate> delegate;
@property (strong, nonatomic) IBOutlet UILabel *facebookLabel;
@property (strong, nonatomic) IBOutlet UILabel *twitterLabel;
@property (strong, nonatomic) IBOutlet UILabel *mailLabel;

typedef enum {
	TWITTER,
	EMAIL,
	FACEBOOK
} SharingPlace;

- (void) setPopOver:(UIPopoverController *)popover;

- (IBAction) shareOnFacebook:(id)sender;
- (IBAction) shareOnTwitter:(id)sender;
- (IBAction) shareOnEmail:(id)sender;

+ (UIViewController *) share: (SharingPlace) place withUIViewController:(UIViewController *)viewController;

@end
