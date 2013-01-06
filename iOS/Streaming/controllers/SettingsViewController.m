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

@synthesize settings;
@synthesize tableView;

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
		self.title = NSLocalizedString(@"CONTROLLER_TITLE_SETTINGS", @"");

		enableRoaming = [[UISwitch alloc] initWithFrame:CGRectZero];
		[enableRoaming addTarget:self action:@selector(switchValueChanged:) forControlEvents:UIControlEventValueChanged];
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

	NSString *filePath = [self getDataFilePath];
	if([[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
		NSLog(@"file exists");
		settings = [[NSMutableDictionary alloc] initWithContentsOfFile:filePath];
	} else {
		NSLog(@"file does not exists");
		settings = [[NSMutableDictionary alloc] init];
	}
}

- (void) viewDidAppear:(BOOL)animated
{
	if([DeviceUtils isPhone]) {
		UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(closeMe)];
		self.navigationItem.rightBarButtonItem = doneButton;
	}
}

- (NSString *) getDataFilePath
{
	NSArray *path = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [path objectAtIndex:0];
	return [documentsDirectory stringByAppendingPathComponent:kPlistname];
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

	switch(numberOfSection) {
		case 0:
			if(numberOfRow == 0) {
				cell.textLabel.text = NSLocalizedString(@"PREF_NETWORK_TYPE", @"");
				id networkTypeAsId = [settings objectForKey:@"PREF_NETWORK_TYPE"];
				NetworkType savedNetworkType = [networkTypeAsId intValue];
				if(savedNetworkType == WIFI_ONLY) {
					cell.detailTextLabel.text = NSLocalizedString(@"WIFI_ONLY", @"");
				} else {
					cell.detailTextLabel.text = NSLocalizedString(@"WIFI_AND_MOBILE", @"");
				}

				cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
			} else if(numberOfRow == 1) {
				cell.textLabel.text = NSLocalizedString(@"PREF_COVER_NETWORK", @"");
				id networkTypeAsId = [settings objectForKey:@"PREF_COVER_NETWORK"];
				NetworkType savedNetworkType = [networkTypeAsId intValue];
				if(savedNetworkType == WIFI_ONLY) {
					cell.detailTextLabel.text = NSLocalizedString(@"WIFI_ONLY", @"");
				} else if(savedNetworkType == NEVER) {
					cell.detailTextLabel.text = NSLocalizedString(@"NEVER", @"");
				} else {
					cell.detailTextLabel.text = NSLocalizedString(@"WIFI_AND_MOBILE", @"");
				}

				cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
			} else if(numberOfRow == 2) {
				cell.textLabel.text = NSLocalizedString(@"PREF_ENABLE_ROAMING", @"");
				id roamingEnabledAsId = [settings objectForKey:@"PREF_ENABLE_ROAMING"];
				NSLog(@"roamingEnabledAsId is %@", roamingEnabledAsId == nil ? @"nil" : @"not nil");
				BOOL roamingEnabled = [roamingEnabledAsId boolValue];
				NSLog(@"roamingEnabled: %d", roamingEnabled);
				enableRoaming.on = roamingEnabled;

				cell.accessoryView = enableRoaming;
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
				id networkTypeAsId = [settings objectForKey:@"PREF_NETWORK_TYPE"];
				NetworkType savedNetworkType = [networkTypeAsId intValue];
				networkTypeChooseViewController = [[NetworkTypeChooseViewController alloc] initWithPreference:NETWORK_TYPE andCurrentValue:savedNetworkType];
				networkTypeChooseViewController.delegate = self;
				[self.navigationController pushViewController:networkTypeChooseViewController animated:YES];
			} else if(numberOfRow == 1) {
				NetworkTypeChooseViewController *networkTypeChooseViewController;
				id networkTypeAsId = [settings objectForKey:@"PREF_COVER_NETWORK"];
				NetworkType savedNetworkType = [networkTypeAsId intValue];
				networkTypeChooseViewController = [[NetworkTypeChooseViewController alloc] initWithPreference:COVER_DOWNLOAD_NETWORK andCurrentValue:savedNetworkType];
				networkTypeChooseViewController.delegate = self;
				[self.navigationController pushViewController:networkTypeChooseViewController animated:YES];
			}
			break;

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
		[settings setObject:[NSNumber numberWithInt:selectedNetworkType] forKey:@"PREF_NETWORK_TYPE"];
		row = 0;
    } else if(preference == COVER_DOWNLOAD_NETWORK) {
		NSLog(@"Cover Network");
		[settings setObject:[NSNumber numberWithInt:selectedNetworkType] forKey:@"PREF_COVER_NETWORK"];
		row = 1;
	}

	NSIndexPath *indexPath = [NSIndexPath indexPathForRow:row inSection:0];
    [tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationNone];

	BOOL result = [settings writeToFile:[self getDataFilePath] atomically:YES];
	NSLog(@"result: %@", result ? @"OK" : @"ERROR");
}

- (void) switchValueChanged:(id) sender
{
	NSLog(@"switchValueChanged");
	UISwitch *selectedSwitch = (UISwitch *)sender;
    
    if([selectedSwitch isEqual:enableRoaming]) {
		NSLog(@"Changed enableRoaming");
		[settings setObject:[NSNumber numberWithBool:selectedSwitch.on] forKey:@"PREF_ENABLE_ROAMING"];
		BOOL result = [settings writeToFile:[self getDataFilePath] atomically:YES];
		NSLog(@"result: %@", result ? @"OK" : @"ERROR");
    }
}

- (void)viewDidUnload {
	tableView = nil;
	[super viewDidUnload];
}

@end
