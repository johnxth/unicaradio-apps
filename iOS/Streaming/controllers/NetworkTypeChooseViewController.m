//
//  NetworkTypeChooseViewController.m
//  Streaming
//
//  Created by Paolo on 03/01/13.
//
//

#import "NetworkTypeChooseViewController.h"

@interface NetworkTypeChooseViewController ()

@end

@implementation NetworkTypeChooseViewController

@synthesize delegate;

- (id)initWithPreference:(Preferences)p andCurrentValue:(NetworkType)value
{
	NSString *nibName = @"NetworkTypeChooseViewController";
    self = [super initWithNibName:nibName bundle:nil];
    if (self) {
        self.title = NSLocalizedString(@"CHOOSE_NETWORK_TYPE", @"");
		self->preference = p;
		self->currentNetworkType = value;
    }

    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

	[self.tableView setBackgroundView:nil];
	[self.tableView setBackgroundColor:[UIColor blackColor]];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	if(preference == NETWORK_TYPE) {
		return 2;
	} else if(preference == COVER_DOWNLOAD_NETWORK) {
		return 3;
	}

	return 0;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	return NSLocalizedString(@"PREF_NETWORK_TYPE", @"");
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	NSLog(@"cellForRowAtIndexPath");
    NSInteger numberOfRow = [indexPath row];

	UITableViewCell *cell;
	cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:nil];
	cell.selectionStyle = UITableViewCellSelectionStyleBlue;

	UIColor *selectedItemTextColor = [UIColor colorWithRed:0x38/255.0 green:0x54/255.0 blue:0x87/255.0 alpha:1.0];
	switch(numberOfRow) {
		case 0:
			cell.textLabel.text = NSLocalizedString(@"WIFI_AND_MOBILE", @"");
			if(currentNetworkType == WIFI_MOBILE) {
				cell.accessoryType = UITableViewCellAccessoryCheckmark;
				cell.textLabel.textColor = selectedItemTextColor;
				selectedTableViewCell = cell;
			}
			break;
		case 1:
			cell.textLabel.text = NSLocalizedString(@"WIFI_ONLY", @"");
			if(currentNetworkType == WIFI_ONLY) {
				cell.accessoryType = UITableViewCellAccessoryCheckmark;
				cell.textLabel.textColor = selectedItemTextColor;
				selectedTableViewCell = cell;
			}
			break;
		case 2:
			cell.textLabel.text = NSLocalizedString(@"NEVER", @"");
			if(currentNetworkType == NEVER) {
				cell.accessoryType = UITableViewCellAccessoryCheckmark;
				cell.textLabel.textColor = selectedItemTextColor;
				selectedTableViewCell = cell;
			}
			break;

		default:
			cell.textLabel.text = @"TEST";
			cell.detailTextLabel.text = @"DETAIL";
			break;
	}
	
	return  cell;
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

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	[tableView deselectRowAtIndexPath:indexPath animated:YES];

	selectedTableViewCell.textLabel.textColor = [UIColor blackColor];
	selectedTableViewCell.accessoryType = UITableViewCellAccessoryNone;

	UITableViewCell *cell;
	cell = [tableView cellForRowAtIndexPath:indexPath];
	cell.accessoryType = UITableViewCellAccessoryCheckmark;
	selectedTableViewCell = cell;

	UIColor *selectedItemTextColor = [UIColor colorWithRed:0x38/255.0 green:0x54/255.0 blue:0x87/255.0 alpha:1.0];
	cell.textLabel.textColor = selectedItemTextColor;

	[delegate networkTypeChangedForPreference:preference andNetworkType:indexPath.row];
}

- (void)viewDidUnload {
	[self setTableView:nil];
	[super viewDidUnload];
}
@end
