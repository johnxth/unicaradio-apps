//
//  SettingsViewController.h
//  Streaming
//
//  Created by Paolo on 02/01/13.
//
//

#import <UIKit/UIKit.h>
#import "NetworkTypeChooseViewController.h"
#import "../managers/SettingsManager.h"

@interface SettingsViewController : UIViewController <UITableViewDelegate, UITableViewDataSource, UINavigationControllerDelegate, NetworkTypeChooseViewControllerDelegate>
{
	IBOutlet UITableView *tableView;

	UISwitch *enableRoaming;

	SettingsManager *settingsManager;
}

@property (strong, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) SettingsManager *settingsManager;

+ (UIViewController *) createSettingsController;

@end
