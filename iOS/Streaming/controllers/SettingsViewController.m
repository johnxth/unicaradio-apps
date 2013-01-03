//
//  SettingsViewController.m
//  Streaming
//
//  Created by Paolo on 02/01/13.
//
//

#import "SettingsViewController.h"
#import "NoItemSelectedViewController.h"

#import "../widgets/UnicaradioUINavigationController.h"

#import "../utils/DeviceUtils.h"

@interface SettingsViewController ()

@end

@implementation SettingsViewController

+ (UIViewController *) createSettingsController
{
	SettingsViewController *settingsViewController = [[SettingsViewController alloc] initWithNibName:@"SettingsViewController_iPhone" bundle:nil];

	UINavigationController *navigationController;
	if([DeviceUtils isPhone]) {
		navigationController = [[UnicaradioUINavigationController alloc] initWithRootViewController:settingsViewController];
	} else {
		navigationController = [[UINavigationController alloc] initWithRootViewController:settingsViewController];
	}

	return navigationController;
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
   self = [super initWithNibName:nibNameOrNil bundle:nil];
    if (self) {
		self.title = @"Settings";

		enableRoaming = [[UISwitch alloc] initWithFrame:CGRectZero];
    }
    return self;
}

- (void) closeMe
{
	[self dismissModalViewControllerAnimated:YES];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
}

- (void) viewDidAppear:(BOOL)animated
{
	if([DeviceUtils isPhone]) {
		UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(closeMe)];
		self.navigationItem.rightBarButtonItem = doneButton;
	}
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    NSString *title = @"";

    switch (section) {
        case 0:
            title = @"Rete";
            break;
        case 1:
            title = @"Informazioni";
            break;
		default:
			break;
    }

    return title;
}


- (NSInteger)tableView:(UITableView *)table numberOfRowsInSection:(NSInteger)section
{
    NSInteger rows = 0;

    switch (section) {
        case 0:
            rows = 3;
            break;
        case 1:
            rows = 1;
            break;
        default:
            break;
    }

    return rows;
}

- (UITableViewCell *)tableView:(UITableView *)_tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	NSLog(@"cellForRowAtIndexPath");
    NSInteger numberOfSection = [indexPath section];
    NSInteger numberOfRow = [indexPath row];

	UITableViewCell *cell;
	cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:nil];
	cell.selectionStyle = UITableViewCellSelectionStyleNone;

	switch(numberOfSection) {
		case 0:
			if(numberOfRow == 0) {
				cell.textLabel.text = @"Tipo";
				cell.detailTextLabel.text = @"WiFi e Mobile";
				cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
			} else if(numberOfRow == 1) {
				cell.textLabel.text = @"Download cover";
				cell.detailTextLabel.text = @"WiFi e Mobile";
				cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
			} else if(numberOfRow == 2) {
				cell.textLabel.text = @"Roaming";
				cell.accessoryView = enableRoaming;
			} else {
				cell.textLabel.text = @"TEST";
				cell.detailTextLabel.text = @"DETAIL";
			}
			break;
			
		default:
			cell.textLabel.text = @"TEST";
			cell.detailTextLabel.text = @"DETAIL";
			break;
	}

	return  cell;
}

@end
