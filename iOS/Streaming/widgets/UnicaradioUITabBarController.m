//
//  UnicaradioUITabBarController.m
//  Streaming
//
//  Created by Paolo on 03/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "UnicaradioUITabBarController.h"
#import <QuartzCore/QuartzCore.h>
#import "../utils/DeviceUtils.h"
#import "UnicaradioUITabBar.h"
#import "UnicaradioUITabBarIos7.h"

#import "SystemUtils.h"

@interface UnicaradioUITabBarController (private)
- (UITabBar *)tabBar;
@end

@implementation UnicaradioUITabBarController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        [self setValue:[self createTabBar] forKeyPath:@"tabBar"];
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
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
	if([DeviceUtils isPhone]) {
		return YES;
	} else {
		return [DeviceUtils isLandscape:interfaceOrientation];
	}
}

-(NSUInteger)supportedInterfaceOrientations
{
	if([DeviceUtils isPhone]) {
		return UIInterfaceOrientationMaskAll;
	} else {
		return UIInterfaceOrientationMaskLandscape;
	}
}

- (UITabBar *) createTabBar
{
	if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
		return [[UnicaradioUITabBarIos7 alloc] initWithFrame:self.tabBar.frame];
	} else {
		return [[UnicaradioUITabBar alloc] initWithFrame:self.tabBar.frame];
	}
}

@end
