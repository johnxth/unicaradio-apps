//
//  AppDelegate.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "AppDelegate.h"

#import "widgets/UnicaradioUITabBarController.h"
#import "widgets/UnicaradioUINavigationController.h"

#import "controllers/StreamingViewController.h"
#import "controllers/ScheduleViewController.h"
#import "controllers/SongRequestViewController.h"
#import "controllers/FavouritesViewController.h"
#import "controllers/InfoViewController.h"
#import "controllers/NoItemSelectedViewController.h"
#import "controllers/ScheduleSplitViewController.h"

#import "utils/DeviceUtils.h"

#import <QuartzCore/QuartzCore.h>

@implementation AppDelegate

@synthesize window = _window;
@synthesize tabBarController = _tabBarController;
@synthesize uiIsVisible;

- (void)dealloc
{
    [_window release];
    [_tabBarController release];
    [super dealloc];
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window = [[[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]] autorelease];
    // Override point for customization after application launch.
    UIViewController *streamingController, *scheduleController, *songRequestController, *favouritesController, *infoController;

	self.tabBarController = [[[UnicaradioUITabBarController alloc] init] autorelease];
	streamingController = [self createStreamingController];

	NSString *nibNameTail;
    if([DeviceUtils isPhone]) {
		nibNameTail = @"_iPhone";
		scheduleController = [self createScheduleControllerForIPhone];
    } else {
		nibNameTail = @"_iPad";
		scheduleController = [self createScheduleControllerForIPad];
    }

	songRequestController = [self createSongRequestControllerWithNibNameTail:nibNameTail];
	favouritesController = [self createFavouritesControllerWithNibNameTail:nibNameTail];
	infoController = [[[InfoViewController alloc] initWithNibName:[NSString stringWithFormat:@"InfoViewController%@", nibNameTail] bundle: nil] autorelease];

	self.tabBarController.viewControllers = [NSArray arrayWithObjects:
											 streamingController,
											 scheduleController,
											 songRequestController,
											 favouritesController,
											 infoController,
											 nil];

    self.window.rootViewController = self.tabBarController;
    [self.window makeKeyAndVisible];
    self.uiIsVisible = YES;

    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    self.uiIsVisible = NO;
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    self.uiIsVisible = NO;
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
    self.uiIsVisible = YES;
    UIViewController *currentController = _tabBarController.selectedViewController;
    if([currentController isKindOfClass:[StreamingViewController class]]) {
        StreamingViewController *streamingController = (StreamingViewController *) currentController;
        [streamingController updateUi];
    }
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    self.uiIsVisible = YES;
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    self.uiIsVisible = NO;
}

/*
// Optional UITabBarControllerDelegate method.
- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController
{
}
*/

/*
// Optional UITabBarControllerDelegate method.
- (void)tabBarController:(UITabBarController *)tabBarController didEndCustomizingViewControllers:(NSArray *)viewControllers changed:(BOOL)changed
{
}
*/

#pragma mark - Controller constructors

- (UIViewController *) createStreamingController
{
	StreamingViewController *streamingController;
	streamingController = [[[StreamingViewController alloc] initWithNibName:nil bundle:nil] autorelease];

	UnicaradioUINavigationController *navStreamingController;
	navStreamingController = [[[UnicaradioUINavigationController alloc] initWithRootViewController:streamingController] autorelease];

	return navStreamingController;
}

- (UIViewController *) createScheduleControllerForIPhone
{
	ScheduleViewController *scheduleController;
	scheduleController = [[[ScheduleViewController alloc] initWithNibName:@"ScheduleViewController_iPhone" bundle:nil] autorelease];

	UnicaradioUINavigationController *navScheduleController;
	navScheduleController = [[[UnicaradioUINavigationController alloc] initWithRootViewController:scheduleController] autorelease];

	return navScheduleController;
}

- (UIViewController *) createScheduleControllerForIPad
{
	ScheduleViewController *scheduleController = [[[ScheduleViewController alloc] initWithNibName:@"ScheduleViewController_iPad" bundle:nil] autorelease];
	NoItemSelectedViewController *noItemSelectedViewController = [[[NoItemSelectedViewController alloc] initWithNibName:@"NoItemSelectedViewController_iPad" bundle:nil] autorelease];

	ScheduleSplitViewController *splitScheduleController = [[[ScheduleSplitViewController alloc] init] autorelease];
	splitScheduleController.delegate = scheduleController;
	splitScheduleController.viewControllers = [[NSArray alloc] initWithObjects:scheduleController, noItemSelectedViewController, nil];

	return splitScheduleController;
}

- (UIViewController *) createSongRequestControllerWithNibNameTail:(NSString *)nibNameTail
{
	NSString *nibName = [NSString stringWithFormat:@"SongRequestViewController%@", nibNameTail];
	SongRequestViewController *songRequestController = [[[SongRequestViewController alloc] initWithNibName:nibName bundle: nil] autorelease];

	UnicaradioUINavigationController *navSongRequestController;
	navSongRequestController = [[[UnicaradioUINavigationController alloc] initWithRootViewController:songRequestController] autorelease];

	return navSongRequestController;
}


- (UIViewController *) createFavouritesControllerWithNibNameTail:(NSString *)nibNameTail
{
	NSString *nibName = [NSString stringWithFormat:@"FavouritesViewController%@", nibNameTail];
	FavouritesViewController *favouritesController = [[[FavouritesViewController alloc] initWithNibName:nibName bundle: nil] autorelease];
	
	UnicaradioUINavigationController *navFavouritesController;
	navFavouritesController = [[[UnicaradioUINavigationController alloc] initWithRootViewController:favouritesController] autorelease];
	
	return navFavouritesController;
}

@end
