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
#import "../widgets/UnicaradioUINavigationBar.h"

@interface ScheduleViewController ()

@end

@implementation ScheduleViewController

@synthesize scheduleTable;
@synthesize navigationBar;

@synthesize days;
@synthesize state;
@synthesize schedule;
@synthesize currentID;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"Schedule", @"Schedule");
        self.tabBarItem.image = [UIImage imageNamed:@"schedule"];
		self.state = DAYS;

		queue = [[NSOperationQueue alloc] init];
		[queue setMaxConcurrentOperationCount: 1];
    }

    return self;
}

- (id)initWithSchedule:(Schedule *)s andDayName:(NSString*)dayName andNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
	self = [self initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
	self.schedule = s;
	self.state = TRANSMISSIONS;
	self.title = dayName;

	return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.

	if(self.schedule == nil) {
		self.days = [[NSMutableArray alloc] initWithObjects:@"Lunedì", @"Martedì",
			 @"Mercoledì", @"Giovedì", @"Venerdì", @"Sabato", @"Domenica", nil];
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

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
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
	cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:nil] autorelease];

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

	UIView *redColorView = [[[UIView alloc] init] autorelease];
	redColorView.backgroundColor = [UIColor colorWithRed:0xA8/255.0 green:0 blue:0 alpha:0.70];
	cell.selectedBackgroundView = redColorView;

	return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	//NSLog([NSString stringWithFormat:@"selected: %d", [indexPath row]]);
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
	if(self.state == TRANSMISSIONS) {
		return;
	}

	self.currentID = indexPath.row;
	
	NSString *dayString = [[[self.scheduleTable cellForRowAtIndexPath:indexPath] textLabel] text];
	//[self showBackButtonWithNavigationTitle:dayString andButtonTitle:@"\u25C1 Schedule"];
	//[self.scheduleTable reloadData];
	
	ScheduleViewController *scheduleViewController = [[ScheduleViewController alloc] initWithSchedule:schedule andDayName:dayString andNibName:self.nibName bundle:self.nibBundle];
	[self.navigationController pushViewController:scheduleViewController animated:YES];
	[scheduleViewController release];
}

- (void) showBackButtonWithNavigationTitle: (NSString *)title andButtonTitle: (NSString *)buttonTitle
{
	UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:buttonTitle
											style:UIBarButtonItemStyleBordered target:self action:@selector(backPressed)];
	UINavigationItem *item = [[UINavigationItem alloc] initWithTitle:title];
	//item.backBarButtonItem = backButton;
	[item setLeftBarButtonItem:backButton];

	[self.navigationBar pushNavigationItem:item animated:YES];
	[item release];
	[backButton release];
	
	//self.navigationBar.topItem.titleView = label;
}

-  (void) backPressed
{
	NSLog(@"backPressed");

	[self.navigationBar popNavigationItemAnimated:YES];
	self.state = DAYS;
	self.currentID = -1;
	[self.scheduleTable reloadData];
}

@end
