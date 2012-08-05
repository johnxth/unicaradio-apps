//
//  SecondViewController.h
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

#define SCHEDULE_URL @"http://www.unicaradio.it/regia/test/palinsesto.php"

@interface ScheduleViewController : UIViewController<UITableViewDelegate, UITableViewDataSource>
{
	NSMutableArray *days;
	IBOutlet UITableView *scheduleTable;
	NSData *scheduleJSON;
}

@property (nonatomic, retain) NSMutableArray *days;
@property (nonatomic, retain) IBOutlet UITableView *scheduleTable;
@property (nonatomic, retain) NSData *scheduleJSON;

@end
