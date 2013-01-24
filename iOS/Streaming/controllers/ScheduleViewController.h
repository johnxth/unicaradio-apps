//
//  SecondViewController.h
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "UnicaradioBaseViewController.h"

#import "../models/Schedule.h"
#import "../enums/Schedule_state.h"

#define SCHEDULE_URL @"http://www.unicaradio.it/regia/test/palinsesto.php"

@interface ScheduleViewController : UnicaradioBaseViewController<UITableViewDelegate,
																 UITableViewDataSource,
																 UISplitViewControllerDelegate>
{
	IBOutlet UITableView *scheduleTable;

	NSOperationQueue *queue;
	BOOL isDownloading;
	int clickedItem;

	NSMutableArray *days;
	ScheduleState state;
	Schedule *schedule;
	NSInteger currentID;
}

@property (nonatomic, strong) IBOutlet UITableView *scheduleTable;

@property (nonatomic, strong) NSMutableArray *days;
@property (nonatomic) ScheduleState state;
@property (nonatomic, strong) Schedule *schedule;
@property (nonatomic) NSInteger currentID;

- (id) initWithSchedule:(Schedule *)schedule andTitle:(NSString*)title andDayNumber:(NSInteger)dayNumberZeroIndexed andNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil;

@end
