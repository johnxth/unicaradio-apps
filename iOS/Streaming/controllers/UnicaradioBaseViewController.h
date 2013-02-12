//
//  UnicaradioBaseViewController.h
//  Streaming
//
//  Created by Paolo on 21/01/13.
//
//

#import <UIKit/UIKit.h>

#import "SettingsViewController.h"
#import "ShareViewController.h"
#import "DeviceUtils.h"

#import <MessageUI/MessageUI.h>

@interface UIViewController (UnicaradioButtons) <UIActionSheetDelegate, MFMailComposeViewControllerDelegate>
{
}

@property (strong) UIPopoverController *popover;
@property (strong) UIPopoverController *sharePopover;

- (void) initButtonBarItemsForNavigationItem:(UINavigationItem *)navigationItem;
- (void) initButtonBarItems;

@end
