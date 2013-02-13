//
//  LicenceViewController.m
//  Streaming
//
//  Created by Paolo on 13/02/13.
//
//

#import "LicenceViewController.h"

@interface LicenceViewController ()

@end

@implementation LicenceViewController

- (id)init
{
    self = [super initWithNibName:@"LicenceViewController" bundle:nil];
    if (self) {
		self.title = NSLocalizedString(@"LIBRARIES_AND_LICENCES", @"");
    }
    return self;
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 6;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell;
	cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];

	switch(indexPath.row) {
		case 0:
			cell.textLabel.text = @"ustwo - US2FormValidator";
			cell.detailTextLabel.text = @"MIT Licence";
			break;
		case 1:
			cell.textLabel.text = @"instructure - CKRefreshControl";
			cell.detailTextLabel.text = @"MIT Licence";
			break;
		case 2:
			cell.textLabel.text = @"DigitalDJ - AudioStreamer";
			cell.detailTextLabel.text = @"zlib Licence";
			break;
		case 3:
			cell.textLabel.text = @"johnezang - JSONKit";
			cell.detailTextLabel.text = @"BSD/Apache 2.0 Licence";
			break;
		case 4:
			cell.textLabel.text = @"cbpowell - MarqueeLabel";
			cell.detailTextLabel.text = @"MIT Licence";
			break;
		case 5:
			cell.textLabel.text = @"tonymillion - Reachability";
			cell.detailTextLabel.text = @"MIT Licence";
			break;
		default:
			break;
	}

    return cell;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];

	NSString *url;
	switch(indexPath.row) {
		case 0:
			url = @"https://github.com/ustwo/US2FormValidator";
			break;
		case 1:
			url = @"https://github.com/instructure/CKRefreshControl";
			break;
		case 2:
			url = @"https://github.com/DigitalDJ/AudioStreamer";
			break;
		case 3:
			url = @"https://github.com/johnezang/JSONKit";
			break;
		case 4:
			url = @"https://github.com/cbpowell/MarqueeLabel";
			break;
		case 5:
			url = @"https://github.com/tonymillion/Reachability";
			break;

	}

	[[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]];
}

@end
