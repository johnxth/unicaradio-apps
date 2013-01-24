//
//  UnicaradioBaseViewController.h
//  Streaming
//
//  Created by Paolo on 21/01/13.
//
//

#import <UIKit/UIKit.h>

#import "SettingsViewController.h"
#import "DeviceUtils.h"

@interface UnicaradioBaseViewController : UIViewController
{
	UIPopoverController *popover;
}

@property (strong) UIPopoverController *popover;

- (void) initButtonBarItemsForNavigationItem:(UINavigationItem *)navigationItem;
- (void) initButtonBarItems;

@end
