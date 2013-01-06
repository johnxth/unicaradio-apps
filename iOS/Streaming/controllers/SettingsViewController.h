//
//  SettingsViewController.h
//  Streaming
//
//  Created by Paolo on 02/01/13.
//
//

#import <UIKit/UIKit.h>
#import "NetworkTypeChooseViewController.h"

#define kPlistname @"Settings.plist"

@interface SettingsViewController : UIViewController <UITableViewDelegate, UITableViewDataSource, UINavigationControllerDelegate, NetworkTypeChooseViewControllerDelegate>
{
	IBOutlet UITableView *tableView;

	UISwitch *enableRoaming;

	NSMutableDictionary *settings;
}

@property (strong, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) NSDictionary *settings;

+ (UIViewController *) createSettingsController;

@end
