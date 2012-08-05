//
//  SecondViewController.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ScheduleViewController.h"
#import "../widgets/DTCustomColoredAccessory.h"
#import "../JSONKit/JSONKit.h"

@interface ScheduleViewController ()

@end

@implementation ScheduleViewController

@synthesize days;
@synthesize scheduleTable;
@synthesize scheduleJSON;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"Schedule", @"Schedule");
        self.tabBarItem.image = [UIImage imageNamed:@"schedule"];
    }
    return self;
}
							
- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
	
	days = [[NSMutableArray alloc] initWithObjects:@"Lunedì", @"Martedì",
			 @"Mercoledì", @"Giovedì", @"Venerdì", @"Sabato", @"Domenica", nil];
/*
	if(scheduleJSON == nil) {
		NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:SCHEDULE_URL]
											 cachePolicy:NSURLRequestReloadIgnoringLocalAndRemoteCacheData
											 timeoutInterval:60.0];
		
		NSURLResponse *response = nil;
		NSError *error = nil;
		scheduleJSON = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
		NSDictionary *resultsDictionary = [scheduleJSON objectFromJSONData];

		NSArray *lunedi = [resultsDictionary objectForKey:@"lunedi"];
		for (NSDictionary *d in lunedi) {
			NSLog([d objectForKey:@"programma"]);
		}
	}
*/

	self.scheduleTable.rowHeight = 55;
	self.scheduleTable.backgroundColor = [UIColor blackColor];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
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
    return [days count];
}

// Setta il contenuto delle varie celle
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cellID"]; 
	
	if (cell == nil){
		cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellSelectionStyleNone reuseIdentifier:@"cellID"] autorelease];
	}

	UIColor *textColor = [UIColor whiteColor];
	//inseriamo nella cello l'elemento della lista corrispondente
	cell.textLabel.text = [days objectAtIndex:indexPath.row];
	cell.backgroundColor = [UIColor clearColor];
	[cell.textLabel setTextColor:textColor];

	UIView *redColorView = [[[UIView alloc] init] autorelease];
	redColorView.backgroundColor = [UIColor colorWithRed:0xA8/255.0 green:0 blue:0 alpha:0.70];
	cell.selectedBackgroundView = redColorView;

	DTCustomColoredAccessory *accessory = [DTCustomColoredAccessory accessoryWithColor:textColor andHighlightedColor:textColor];
	cell.accessoryView = accessory;

	return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	//NSLog([NSString stringWithFormat:@"selected: %d", [indexPath row]]);
}

@end
