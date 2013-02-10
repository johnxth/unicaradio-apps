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
        self.title = @"Scegli rete";
		self->preference = p;
		self->currentNetworkType = value;
    }

    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
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
	return @"Tipo di rete";
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

@end