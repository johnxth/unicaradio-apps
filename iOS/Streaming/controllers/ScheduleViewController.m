//
//  SecondViewController.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <objc/runtime.h>

#import "ScheduleViewController.h"
#import "../widgets/DTCustomColoredAccessory.h"
#import "../libs/JSONKit/JSONKit.h"
#import "../models/Transmission.h"
#import "../operations/DownloadScheduleOperation.h"
#import "UnicaradioUINavigationController.h"

@interface ScheduleViewController ()

@end

@implementation ScheduleViewController

@synthesize scheduleTable;

@synthesize days;
@synthesize state;
@synthesize schedule;
@synthesize currentID;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"CONTROLLER_TITLE_SCHEDULE", @"");
        self.tabBarItem.image = [UIImage imageNamed:@"schedule"];
		self.state = DAYS;

		queue = [[NSOperationQueue alloc] init];
		[queue setMaxConcurrentOperationCount: 1];

		if([DeviceUtils isPhone]) {
			[self initButtonBarItems];
		}
    }

    return self;
}

- (id)initWithSchedule:(Schedule *)s andTitle:(NSString *)t andDayNumber:(NSInteger)dayNumberZeroIndexed andNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
	self = [self initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
	self.schedule = s;
	self.state = TRANSMISSIONS;
	self.title = t;
	self.currentID = dayNumberZeroIndexed;
	[self initButtonBarItems];

	return self;	
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.

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

	self.scheduleTable.rowHeight = 55;
	self.scheduleTable.backgroundColor = [UIColor blackColor];

	self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:0xA8/255.0 green:0 blue:0 alpha:1];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void) viewDidAppear:(BOOL)animated
{
	[super viewDidAppear:animated];
	NSLog(@"ScheduleViewController - viewDidAppear");

	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(receiveNotification:) name:@"GetSchedule" object:nil];

	[self refreshData];
}

- (void) refreshData
{
	if(schedule == nil) {
		DownloadScheduleOperation *operation = [[DownloadScheduleOperation alloc] init];
		[queue addOperation:operation];
	}
}

- (void) receiveNotification: (NSNotification *)notification
{
	NSLog(@"ScheduleViewController - receiveNotification");
	self.schedule = [Schedule fromJSON:[notification object]];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

// Setta il numero di righe della tabella .
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	if(self.state == DAYS) {
		return [days count];
	} else {
		NSArray *transmissionsForCurrentId = [schedule getTransmissionsByDay:currentID];
		return [transmissionsForCurrentId count];
	}
}

// Setta il contenuto delle varie celle
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	UITableViewCell *cell;
	cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:nil];

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

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	//NSLog([NSString stringWithFormat:@"selected: %d", [indexPath row]]);
	if(self.state == TRANSMISSIONS) {
		NSLog(@"TRANSMISSIONS mode. ignoring.");
		[tableView deselectRowAtIndexPath:indexPath animated:YES];
		return;
	}

	NSString *dayString = [[[self.scheduleTable cellForRowAtIndexPath:indexPath] textLabel] text];

	NSInteger dayNumber = indexPath.row;
	NSLog(@"dayNumber: %d", dayNumber);
	if(![NSLocalizedString(@"FIRST_DAY", @"") isEqual:@"1"]) {
		dayNumber = dayNumber - 1 < 0 ? 6 : dayNumber - 1;
		NSLog(@"Uh! First day isn't 1. New dayNumber: %d", dayNumber);
	}

	ScheduleViewController *scheduleViewController = [[ScheduleViewController alloc] initWithSchedule:schedule andTitle:dayString andDayNumber:dayNumber andNibName:self.nibName bundle:self.nibBundle];
	if([DeviceUtils isPhone]) {
		[self.navigationController pushViewController:scheduleViewController animated:YES];
		[tableView deselectRowAtIndexPath:indexPath animated:YES];
	} else {
		UnicaradioUINavigationController *navScheduleController;
		navScheduleController = [[UnicaradioUINavigationController alloc] initWithRootViewController:scheduleViewController];
		NSArray *newVCs = [NSArray arrayWithObjects:[self.splitViewController.viewControllers objectAtIndex:0], navScheduleController, nil];
		self.splitViewController.viewControllers = newVCs;
	}
}

@end
