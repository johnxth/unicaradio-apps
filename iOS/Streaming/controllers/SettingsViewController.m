//
//  SettingsViewController.m
//  Streaming
//
//  Created by Paolo on 02/01/13.
//
//

#import "SettingsViewController.h"
#import "NoItemSelectedViewController.h"
#import "LicenceViewController.h"

#import "../widgets/UnicaradioUINavigationController.h"

#import "../utils/DeviceUtils.h"
#import "SystemUtils.h"

@interface SettingsViewController ()

@end

@implementation SettingsViewController

@synthesize settingsManager;
@synthesize tableView;

+ (UIViewController *) createSettingsController
{
	SettingsViewController *settingsViewController = [[SettingsViewController alloc] initWithNibName:@"SettingsViewController" bundle:nil];

	UINavigationController *navigationController;
	if([DeviceUtils isPhone]) {
		navigationController = [[UnicaradioUINavigationController alloc] initWithRootViewController:settingsViewController];
	} else {
		navigationController = [[UINavigationController alloc] initWithRootViewController:settingsViewController];
	}

//	if([SystemUtils isIos7]) {
//		[navigationController.navigationBar drawRect:CGRectMake(0, 0, 0, 0)];
//	}

	return navigationController;
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
   self = [super initWithNibName:nibNameOrNil bundle:nil];
	if(self) {
		self.title = NSLocalizedString(@"CONTROLLER_TITLE_SETTINGS", @"");

		settingsManager = [SettingsManager getInstance];
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

	[self.tableView setBackgroundView:nil];
	[self.tableView setBackgroundColor:[UIColor blackColor]];
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
            title = NSLocalizedString(@"CATEGORY_NETWORK", @"");
            break;
        case 1:
            title = NSLocalizedString(@"CATEGORY_INFO", @"");
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
            rows = 2;
            break;
        case 1:
            rows = 2;
            break;
        default:
            break;
    }

    return rows;
}

- (UIView *)tableView:(UITableView *)_tableView viewForHeaderInSection:(NSInteger)section {
	NSString *sectionTitle = [self tableView:_tableView titleForHeaderInSection:section];
	if (sectionTitle == nil) {
		return nil;
	}

	// Create label with section title
	UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(20, 0, _tableView.bounds.size.width - 40, 30)];
	//If you add a bit to x and decrease y, it will be more in line with the tableView cell (that is in iPad and landscape)
	label.backgroundColor = [UIColor clearColor];
	label.textColor = [UIColor whiteColor];
	label.font = [UIFont boldSystemFontOfSize:18];
	label.text = sectionTitle;

	// Create header view and add label as a subview
	UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 300, 30)];
	[view addSubview:label];

	return view;
}

- (UITableViewCell *)tableView:(UITableView *)_tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	NSLog(@"cellForRowAtIndexPath");
    NSInteger numberOfSection = [indexPath section];
    NSInteger numberOfRow = [indexPath row];

	UITableViewCell *cell;
	cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:nil];

	switch(numberOfSection) {
		case 0:
			if(numberOfRow == 0) {
				cell.textLabel.text = NSLocalizedString(@"PREF_NETWORK_TYPE", @"");
				NetworkType savedNetworkType = [settingsManager getNetworkType];
				if(savedNetworkType == WIFI_ONLY) {
					cell.detailTextLabel.text = NSLocalizedString(@"WIFI_ONLY", @"");
				} else {
					cell.detailTextLabel.text = NSLocalizedString(@"WIFI_AND_MOBILE", @"");
				}

				cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
			} else if(numberOfRow == 1) {
				cell.textLabel.text = NSLocalizedString(@"PREF_COVER_NETWORK", @"");
				NetworkType savedNetworkType = [settingsManager getNetworkTypeForCover];
				if(savedNetworkType == WIFI_ONLY) {
					cell.detailTextLabel.text = NSLocalizedString(@"WIFI_ONLY", @"");
				} else if(savedNetworkType == NEVER) {
					cell.detailTextLabel.text = NSLocalizedString(@"NEVER", @"");
				} else {
					cell.detailTextLabel.text = NSLocalizedString(@"WIFI_AND_MOBILE", @"");
				}

				cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
			}
			break;

		case 1:
			if(numberOfRow == 0) {
				NSString *appVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey: @"CFBundleShortVersionString"];
				cell.textLabel.text = NSLocalizedString(@"APP_VERSION", @"");
				cell.detailTextLabel.text = appVersion;
			} else if(numberOfRow == 1) {
				cell.textLabel.text = NSLocalizedString(@"LIBRARIES_AND_LICENCES", @"");
			}
			break;
		default:
			cell.textLabel.text = @"TEST";
			cell.detailTextLabel.text = @"DETAIL";
			break;
	}

	return  cell;
}

- (void)tableView:(UITableView *)_tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	NSLog(@"cellForRowAtIndexPath");
    NSInteger numberOfSection = [indexPath section];
    NSInteger numberOfRow = [indexPath row];

	[tableView deselectRowAtIndexPath:indexPath animated:YES];

	switch(numberOfSection) {
		case 0:
			if(numberOfRow == 0) {
				NetworkTypeChooseViewController *networkTypeChooseViewController;
				NetworkType savedNetworkType = [settingsManager getNetworkType];
				networkTypeChooseViewController = [[NetworkTypeChooseViewController alloc] initWithPreference:NETWORK_TYPE andCurrentValue:savedNetworkType];
				networkTypeChooseViewController.delegate = self;
				[self.navigationController pushViewController:networkTypeChooseViewController animated:YES];
			} else if(numberOfRow == 1) {
				NetworkTypeChooseViewController *networkTypeChooseViewController;
				NetworkType savedNetworkType = [settingsManager getNetworkTypeForCover];
				networkTypeChooseViewController = [[NetworkTypeChooseViewController alloc] initWithPreference:COVER_DOWNLOAD_NETWORK andCurrentValue:savedNetworkType];
				networkTypeChooseViewController.delegate = self;
				[self.navigationController pushViewController:networkTypeChooseViewController animated:YES];
			}
			break;
		case 1:
			if(numberOfRow == 1) {
				LicenceViewController *licenceViewController;
				licenceViewController = [[LicenceViewController alloc] init];
				[self.navigationController pushViewController:licenceViewController animated:YES];
			}
		default:
			break;
	}
}

- (void) networkTypeChangedForPreference:(Preferences)preference andNetworkType:(NetworkType)selectedNetworkType
{
	NSLog(@"networkTypeChangedForPreference");

	NSInteger row = 0;
    if(preference == NETWORK_TYPE) {
		NSLog(@"Network type");
		[settingsManager saveNetworkType:selectedNetworkType];
		row = 0;
    } else if(preference == COVER_DOWNLOAD_NETWORK) {
		NSLog(@"Cover Network");
		[settingsManager saveNetworkTypeForCover:selectedNetworkType];
		row = 1;
	}

	NSIndexPath *indexPath = [NSIndexPath indexPathForRow:row inSection:0];
    [tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationNone];
}

- (void)viewDidUnload {
	tableView = nil;
	[super viewDidUnload];
}

@end
