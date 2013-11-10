//
//  ScheduleTableViewController.h
//  Streaming
//
//  Created by Paolo on 09/02/13.
//
//

#import <UIKit/UIKit.h>

#import "Schedule_state.h"
#import "Schedule.h"

#import "UnicaradioBaseViewController.h"

#define SCHEDULE_URL @"http://www.unicaradio.it/regia/test/palinsesto.php"

@interface ScheduleTableViewController : UITableViewController<UISplitViewControllerDelegate>
{	
	NSOperationQueue *queue;
	BOOL isDownloading;
	int clickedItem;
	
	NSMutableArray *days;
	ScheduleState state;
	Schedule *schedule;
	NSInteger currentID;
	
	SettingsManager *settingsManager;
}

@property (nonatomic, strong) NSMutableArray *days;
@property (nonatomic) ScheduleState state;
@property (nonatomic, strong) Schedule *schedule;
@property (nonatomic) NSInteger currentID;
@property (nonatomic, strong) SettingsManager *settingsManager;

- (id) initWithSchedule:(Schedule *)schedule
			   andTitle:(NSString*)title
		   andDayNumber:(NSInteger)dayNumberZeroIndexed
			 andNibName:(NSString *)nibNameOrNil
				 bundle:(NSBundle *)nibBundleOrNil;

@end
