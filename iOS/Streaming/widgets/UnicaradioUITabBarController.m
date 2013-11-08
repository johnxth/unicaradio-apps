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

#import "SystemUtils.h"

@interface UnicaradioUITabBarController (private)
- (UITabBar *)tabBar;
@end

@implementation UnicaradioUITabBarController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        [self setValue:[[UnicaradioUITabBar alloc] initWithFrame:self.tabBar.frame] forKeyPath:@"tabBar"];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
	
	/*UIColor *startRed = [UIColor colorWithRed:0xA8/255.0 green:0 blue:0 alpha:1];
    UIColor *middleRed = [UIColor colorWithRed:0x7F/255.0 green:0 blue:0 alpha:1];
    UIColor *endRed = [UIColor colorWithRed:0x69/255.0 green:0 blue:0 alpha:1];
	
	CAGradientLayer *gradientNavBar = [CAGradientLayer layer];
	gradientNavBar.colors = [NSArray arrayWithObjects:(id)[startRed CGColor], (id)[middleRed CGColor], (id)[endRed CGColor], nil];
	gradientNavBar.frame = self.tabBar.bounds;
	gradientNavBar.name = @"unicaradioUITabBar";
	[self.tabBar.layer insertSublayer:gradientNavBar atIndex:1];
	self.tabBar.tintColor = [UIColor whiteColor];*/
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

@end
