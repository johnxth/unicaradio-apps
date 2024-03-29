//
//  ScheduleTableViewController.m
//  Streaming
//
//  Created by Paolo on 09/02/13.
//
//

#import "ScheduleTableViewController.h"

#import <CKRefreshControl/CKRefreshControl.h>

#import "DTCustomColoredAccessory.h"
#import "JSONKit.h"
#import "Transmission.h"
#import "DownloadScheduleOperation.h"
#import "UnicaradioUINavigationController.h"
#import "ScheduleTableViewCell.h"
#import "DeviceUtils.h"

#import "NoItemSelectedViewController.h"

#import "SystemUtils.h"
#import "NetworkUtils.h"

#import "RefreshType.h"

#import "Error.h"

@interface ScheduleTableViewController ()

@end

@implementation ScheduleTableViewController

@synthesize days;
@synthesize state;
@synthesize schedule;
@synthesize currentID;

@synthesize settingsManager;

- (void)initCommon
{
	self.title = NSLocalizedString(@"CONTROLLER_TITLE_SCHEDULE", @"");
	self.tabBarItem.image = [UIImage imageNamed:@"schedule"];
	self.state = DAYS;
	self.currentID = -1;
	
	settingsManager = [SettingsManager getInstance];
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
		[self initCommon];

		self.refreshControl = [[UIRefreshControl alloc] init];
		if(SYSTEM_VERSION_LESS_THAN(@"6.0")) {
			//FIXME: attributedTitle not always shown in iOS6
			self.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"PULL_TO_REFRESH_MESSAGE", @"")];
		}
		self.refreshControl.tintColor = [UIColor whiteColor];
		[self.refreshControl addTarget:self action:@selector(doRefresh:) forControlEvents:UIControlEventValueChanged];

		queue = [[NSOperationQueue alloc] init];
		[queue setMaxConcurrentOperationCount: 1];

		if([DeviceUtils isPhone]) {
			[self initButtonBarItems];
		}

    }
    return self;
}

- (void)doRefresh:(CKRefreshControl *)sender {
    NSLog(@"refreshing");
	if(SYSTEM_VERSION_LESS_THAN(@"6.0")) {
		//FIXME: attributedTitle not always shown in iOS6
		self.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"PULL_TO_REFRESH_REFRESHING", @"")];
	}

	[self replaceRightWithDefaultView];
    [self refreshData:FORCED];
}

- (id)initWithSchedule:(Schedule *)s andTitle:(NSString *)t andDayNumber:(NSInteger)dayNumberZeroIndexed andNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
	self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
	if(self) {
		[self initCommon];
		self.schedule = s;
		self.state = TRANSMISSIONS;
		self.title = t;
		self.currentID = dayNumberZeroIndexed;
		[self initButtonBarItems];
	}

	return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

	if(self.schedule == nil) {
		if([NSLocalizedString(@"FIRST_DAY", @"") isEqual:@"1"]) {
			self.days = [[NSMutableArray alloc] initWithObjects:
						 NSLocalizedString(@"DAYS_MONDAY", @""),
						 NSLocalizedString(@"DAYS_TUESDAY", @""),
						 NSLocalizedString(@"DAYS_WEDNESDAY", @""),
						 NSLocalizedString(@"DAYS_THURSDAY", @""),
						 NSLocalizedString(@"DAYS_FRIDAY", @""),
						 NSLocalizedString(@"DAYS_SATURDAY", @""),
						 NSLocalizedString(@"DAYS_SUNDAY", @""),
						 nil];
		} else {
			self.days = [[NSMutableArray alloc] initWithObjects:
						 NSLocalizedString(@"DAYS_SUNDAY", @""),
						 NSLocalizedString(@"DAYS_MONDAY", @""),
						 NSLocalizedString(@"DAYS_TUESDAY", @""),
						 NSLocalizedString(@"DAYS_WEDNESDAY", @""),
						 NSLocalizedString(@"DAYS_THURSDAY", @""),
						 NSLocalizedString(@"DAYS_FRIDAY", @""),
						 NSLocalizedString(@"DAYS_SATURDAY", @""),
						 nil];
		}
		self.state = DAYS;
	}

	self.tableView.rowHeight = 55;
	self.tableView.backgroundColor = [UIColor blackColor];

	if(SYSTEM_VERSION_LESS_THAN(@"7.0")) {
		self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:0xA8/255.0 green:0 blue:0 alpha:1];
	}
}

- (void) viewDidAppear:(BOOL)animated
{
	[super viewDidAppear:animated];
	NSLog(@"ScheduleViewController - viewDidAppear");

	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(receiveNotification:) name:@"GetSchedule" object:nil];

	if(![NetworkUtils isConnectionOK:settingsManager]) {
		return;
	}

	if(self.schedule == nil) {
		[self refreshData:NORMAL];
	}
}

- (void) viewWillDisappear:(BOOL)animated
{
	[self replaceRightWithDefaultView];
}

- (void) replaceRightWithDefaultView
{
	NoItemSelectedViewController *noItemSelectedViewController = [[NoItemSelectedViewController alloc] initWithNibName:@"NoItemSelectedViewController_iPad" bundle:nil];
	[self substituteRightController:noItemSelectedViewController];
}

- (void) refreshData:(RefreshType)refreshType
{
	if(refreshType == FORCED && ![NetworkUtils isConnectionOKForGui:settingsManager]) {
		[self stopRefresh];
		return;
	} else if(refreshType == NORMAL && ![NetworkUtils isConnectionOK:settingsManager]) {
		[self stopRefresh];
		return;
	} else if(![NetworkUtils isConnectionOK:settingsManager]) {
		[self stopRefresh];
		return;
	}

	if(![self.refreshControl isRefreshing]) {
		[self.refreshControl performSelector:@selector(beginRefreshing)];
	}
	DownloadScheduleOperation *operation = [[DownloadScheduleOperation alloc] init];
	[queue addOperation:operation];
}

- (void) receiveNotification: (NSNotification *)notification
{
	NSLog(@"ScheduleViewController - receiveNotification");
	[self stopRefresh];

	NSData *json = [notification object];
	[self performSelectorOnMainThread:@selector(refreshCompleted:) withObject:json waitUntilDone:YES];
}

- (void) stopRefresh
{
	[self.refreshControl performSelector:@selector(endRefreshing)];
	if(SYSTEM_VERSION_LESS_THAN(@"6.0")) {
		//FIXME: attributedTitle not always shown in iOS6
		self.refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"PULL_TO_REFRESH_MESSAGE", @"")];
	}
}

- (void) refreshCompleted:(NSData *)serverResponse
{
	if(serverResponse == nil) {
		NSLog(@"nil response");
		NSString *title = NSLocalizedString(@"DIALOG_SEND_FAILED_TITLE", @"");
		NSString *message = NSLocalizedString(@"DIALOG_SEND_FAILED_MESSAGE", @"");
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil];
		[alert show];
		return;
	}

	NSDictionary *resultsDictionary = [serverResponse objectFromJSONData];
	NSNumber *errorCode = [resultsDictionary objectForKey:@"errorCode"];
	NSLog(@"errorCode: %d", [errorCode integerValue]);
	if([errorCode intValue] == NO_ERROR) {
		self.schedule = [Schedule fromJSON:serverResponse];
		NSLog(@"ScheduleViewController - receiveNotification: json parsed");

		if(state == DAYS && currentID != -1) {
			[self drawSecondLevel:currentID];
			currentID = -1;
		}
	} else {
		NSString *title;
		NSString *message;
		if([errorCode intValue] == INTERNAL_DOWNLOAD_ERROR) {
			title = NSLocalizedString(@"DIALOG_CHECK_CONNECTION_TITLE", @"");
			message = NSLocalizedString(@"DIALOG_CHECK_CONNECTION_MESSAGE", @"");
		} else {
			title = NSLocalizedString(@"DIALOG_SEND_FAILED_TITLE", @"");
			message = NSLocalizedString(@"DIALOG_SEND_FAILED_MESSAGE", @"");
		}

		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil];
		[alert show];
	}
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	if(self.state == DAYS) {
		return [days count];
	} else {
		NSArray *transmissionsForCurrentId = [schedule getTransmissionsByDay:currentID];
		return [transmissionsForCurrentId count];
	}
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	/*UITableViewCell *cell;
	cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:nil];*/
	NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"ScheduleTableViewCell" owner:self options:nil];
	ScheduleTableViewCell *cell = [nib objectAtIndex:0];

	UIColor *textColor = [UIColor whiteColor];

	NSInteger index = indexPath.row;
	if(self.state == DAYS) {
		cell.textLabel.text = [days objectAtIndex:index];
		
		DTCustomColoredAccessory *accessory = [DTCustomColoredAccessory accessoryWithColor:textColor andHighlightedColor:textColor];
		cell.accessoryView = accessory;
	} else {
		NSArray *transmissionsForCurrentId = [schedule getTransmissionsByDay:currentID];
		Transmission *transmission = [transmissionsForCurrentId objectAtIndex:index];
		cell.textLabel.text = transmission.formatName;
		cell.textLabel.numberOfLines = 2;
		cell.textLabel.lineBreakMode = UILineBreakModeTailTruncation;

		cell.detailTextLabel.text = transmission.startTime;
	}

	cell.backgroundColor = [UIColor clearColor];
	[cell.textLabel setTextColor:textColor];
	[cell.detailTextLabel setTextColor:textColor];

	UIView *redColorView = [[UIView alloc] init];
	redColorView.backgroundColor = [UIColor colorWithRed:0xA8/255.0 green:0 blue:0 alpha:0.70];
	cell.selectedBackgroundView = redColorView;

	return cell;
}

#pragma mark - Table view delegate

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	//NSLog([NSString stringWithFormat:@"selected: %d", [indexPath row]]);
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
	if(self.state == TRANSMISSIONS) {
		NSLog(@"TRANSMISSIONS mode. ignoring.");
		return;
	}
	if([self.refreshControl isRefreshing]) {
		// is this check useless?
		NSLog(@"Refreshing. ignoring.");
		return;
	}

	NSInteger dayNumber = indexPath.row;
	if(schedule == nil) {
		if([NetworkUtils isConnectionOKForGui:settingsManager]) {
			currentID = dayNumber;
			[self refreshData:FORCED];
		}

		return;
	}

	[self drawSecondLevel:dayNumber];
}

- (void) substituteRightController:(UIViewController *)controller
{
	UnicaradioUINavigationController *navScheduleController;
	navScheduleController = [[UnicaradioUINavigationController alloc] initWithRootViewController:controller];
	NSArray *newViewControllers = [NSArray arrayWithObjects:[self.splitViewController.viewControllers objectAtIndex:0], navScheduleController, nil];
	self.splitViewController.viewControllers = newViewControllers;
}

- (void) drawSecondLevel:(int)position
{
	int dayNumber = position;
	NSString *title = self.days[dayNumber];

	NSLog(@"dayNumber: %d", dayNumber);
	if(![NSLocalizedString(@"FIRST_DAY", @"") isEqual:@"1"]) {
		dayNumber = dayNumber - 1 < 0 ? 6 : dayNumber - 1;
		NSLog(@"Uh! First day isn't 1. New dayNumber: %d", dayNumber);
	}

	ScheduleTableViewController *scheduleViewController =
		[[ScheduleTableViewController alloc] initWithSchedule:schedule
													 andTitle:title
												 andDayNumber:dayNumber
												   andNibName:self.nibName
													   bundle:self.nibBundle];
	if([DeviceUtils isPhone]) {
		[self.navigationController pushViewController:scheduleViewController animated:YES];
	} else {
		[self substituteRightController:scheduleViewController];
	}
}

@end
