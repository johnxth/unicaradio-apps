//
//  SecondViewController.h
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "../models/Schedule.h"
#import "../enums/Schedule_state.h"

#define SCHEDULE_URL @"http://www.unicaradio.it/regia/test/palinsesto.php"

@interface ScheduleViewController : UIViewController<UITableViewDelegate, UITableViewDataSource>
{
	NSMutableArray *days;
	IBOutlet UITableView *scheduleTable;
	ScheduleState state;
	Schedule *schedule;
	NSInteger currentID;
}

@property (nonatomic, retain) NSMutableArray *days;
@property (nonatomic, retain) IBOutlet UITableView *scheduleTable;
@property (nonatomic) ScheduleState state;
@property (nonatomic, retain) Schedule *schedule;
@property (nonatomic) NSInteger currentID;

@end
