//
//  AppDelegate.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "AppDelegate.h"

#import "widgets/UnicaradioUITabBarController.h"

#import "controllers/StreamingViewController.h"
#import "controllers/ScheduleViewController.h"
#import "controllers/SongRequestViewController.h"
#import "controllers/FavouritesViewController.h"
#import "controllers/InfoViewController.h"

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

	streamingController = [[[StreamingViewController alloc] initWithNibName:nil bundle:nil] autorelease];
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
        scheduleController = [[[ScheduleViewController alloc] initWithNibName:@"ScheduleViewController_iPhone" bundle:nil] autorelease];
        songRequestController = [[[SongRequestViewController alloc] initWithNibName:@"SongRequestViewController_iPhone" bundle: nil] autorelease];
        favouritesController = [[[FavouritesViewController alloc] initWithNibName:@"FavouritesViewController_iPhone" bundle: nil] autorelease];
        infoController = [[[InfoViewController alloc] initWithNibName:@"InfoViewController_iPhone" bundle: nil] autorelease];
    } else {
        scheduleController = [[[ScheduleViewController alloc] initWithNibName:@"ScheduleViewController_iPad" bundle:nil] autorelease];
        songRequestController = [[[SongRequestViewController alloc] initWithNibName:@"SongRequestViewController_iPad" bundle: nil] autorelease];
        favouritesController = [[[FavouritesViewController alloc] initWithNibName:@"FavouritesViewController_iPad" bundle: nil] autorelease];
        infoController = [[[InfoViewController alloc] initWithNibName:@"InfoViewController_iPad" bundle: nil] autorelease];
    }
    self.tabBarController = [[[UnicaradioUITabBarController alloc] init] autorelease];
    //self.tabBarController = [[[UITabBarController alloc] init] autorelease];
    self.tabBarController.viewControllers = [NSArray arrayWithObjects:streamingController, scheduleController, songRequestController, favouritesController, infoController, nil];

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

@end
