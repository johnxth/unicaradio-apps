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
#import "controllers/ScheduleTableViewController.h"
#import "controllers/SongRequestViewController.h"
#import "controllers/FavouritesViewController.h"
#import "controllers/InfoViewController.h"
#import "controllers/NoItemSelectedViewController.h"
#import "controllers/ScheduleSplitViewController.h"

#import "utils/DeviceUtils.h"
#import "SystemUtils.h"

#import <QuartzCore/QuartzCore.h>

@implementation AppDelegate

@synthesize window = _window;
@synthesize tabBarController = _tabBarController;
@synthesize uiIsVisible;


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];

    // Override point for customization after application launch.
    UIViewController *streamingController, *scheduleController, *songRequestController, *favouritesController, *infoController;

	self.tabBarController = [[UnicaradioUITabBarController alloc] init];
	self.tabBarController.delegate = self;

	streamingController = [self createStreamingController];

	NSString *nibNameTail;
    if([DeviceUtils isPhone]) {
		nibNameTail = @"_iPhone";
		scheduleController = [self createScheduleControllerForIPhone];
    } else {
		nibNameTail = @"_iPad";
		scheduleController = [self createScheduleControllerForIPad];
    }

	songRequestController = [self createSongRequestController];
	favouritesController = [self createFavouritesControllerWithNibNameTail:nibNameTail];
	infoController = [self createInfoControllerWithNibNameTail:nibNameTail];

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

	updateChecker = [[iUC alloc] init];
	updateChecker.delegate = self;
	updateChecker.updateURL = [NSURL URLWithString:@"http://www.unicaradio.it/regia/test/updates_iOS.php"];
	updateChecker.appStoreURL = [NSURL URLWithString:@"http://itunes.com/apps/unicaradio"];
	[updateChecker checkVersion];

	if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
		[[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
	} else {
		[[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleDefault];
	}

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
    UnicaradioUINavigationController *firstController = [_tabBarController.viewControllers objectAtIndex:0];
	UIViewController *currentController = [firstController.viewControllers objectAtIndex:0];
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


// Optional UITabBarControllerDelegate method.
- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController
{
	if(![viewController isKindOfClass:[UnicaradioUINavigationController class]]) {
		return;
	}

	UnicaradioUINavigationController *currentNavigationController = (UnicaradioUINavigationController *) viewController;
	UIViewController *firstController = [currentNavigationController.viewControllers objectAtIndex:0];
	if(firstController == nil || ![firstController isKindOfClass:[StreamingViewController class]]) {
		return;
	}

	StreamingViewController *streamingController = (StreamingViewController *)firstController;
	[streamingController updateUi];
}

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
	streamingController = [[StreamingViewController alloc] initWithNibName:nil bundle:nil];

	UnicaradioUINavigationController *navStreamingController;
	navStreamingController = [[UnicaradioUINavigationController alloc] initWithRootViewController:streamingController];

	return navStreamingController;
}

- (UIViewController *) createScheduleControllerForIPhone
{
	ScheduleTableViewController *scheduleController;
	scheduleController = [[ScheduleTableViewController alloc] initWithNibName:@"ScheduleViewController_iPhone" bundle:nil];

	UnicaradioUINavigationController *navScheduleController;
	navScheduleController = [[UnicaradioUINavigationController alloc] initWithRootViewController:scheduleController];

	return navScheduleController;
}

- (UIViewController *) createScheduleControllerForIPad
{
	ScheduleTableViewController *scheduleController = [[ScheduleTableViewController alloc] initWithNibName:@"ScheduleViewController_iPad" bundle:nil];
	UnicaradioUINavigationController *navScheduleController;
	navScheduleController = [[UnicaradioUINavigationController alloc] initWithRootViewController:scheduleController];
	NoItemSelectedViewController *noItemSelectedViewController = [[NoItemSelectedViewController alloc] initWithNibName:@"NoItemSelectedViewController_iPad" bundle:nil];
	UnicaradioUINavigationController *navNoItemSelectedController;
	navNoItemSelectedController = [[UnicaradioUINavigationController alloc] initWithRootViewController:noItemSelectedViewController];

	ScheduleSplitViewController *splitScheduleController = [[ScheduleSplitViewController alloc] init];
	splitScheduleController.delegate = scheduleController;
	splitScheduleController.viewControllers = [[NSArray alloc] initWithObjects:navScheduleController, navNoItemSelectedController, nil];

	return splitScheduleController;
}

- (UIViewController *) createSongRequestController
{
	NSString *nibName = @"SongRequestViewController";
	SongRequestViewController *songRequestController = [[SongRequestViewController alloc] initWithNibName:nibName bundle: nil];

	UnicaradioUINavigationController *navSongRequestController;
	navSongRequestController = [[UnicaradioUINavigationController alloc] initWithRootViewController:songRequestController];

	return navSongRequestController;
}

- (UIViewController *) createFavouritesControllerWithNibNameTail:(NSString *)nibNameTail
{
	NSString *nibName = [NSString stringWithFormat:@"FavouritesViewController%@", nibNameTail];
	FavouritesViewController *favouritesController = [[FavouritesViewController alloc] initWithNibName:nibName bundle: nil];
	
	UnicaradioUINavigationController *navFavouritesController;
	navFavouritesController = [[UnicaradioUINavigationController alloc] initWithRootViewController:favouritesController];

	return navFavouritesController;
}

- (UIViewController *) createInfoControllerWithNibNameTail:(NSString *)nibNameTail
{
	NSString *nibName = [NSString stringWithFormat:@"InfoViewController%@", nibNameTail];
	InfoViewController *infoController = [[InfoViewController alloc] initWithNibName:nibName bundle: nil];
	
	UnicaradioUINavigationController *navInfoController;
	navInfoController = [[UnicaradioUINavigationController alloc] initWithRootViewController:infoController];

	return navInfoController;
}

@end
