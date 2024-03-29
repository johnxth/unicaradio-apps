//
//  ScheduleSplitViewController.m
//  Streaming
//
//  Created by Paolo on 24/12/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ScheduleSplitViewController.h"

@interface ScheduleSplitViewController ()

@end

@implementation ScheduleSplitViewController

- (id)init
{
	self = [super init];
	if(self) {
		self.title = NSLocalizedString(@"CONTROLLER_TITLE_SCHEDULE", @"");
        self.tabBarItem.image = [UIImage imageNamed:@"schedule"];
	}

	return self;
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"CONTROLLER_TITLE_SCHEDULE", @"");
        self.tabBarItem.image = [UIImage imageNamed:@"schedule"];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
}

@end
