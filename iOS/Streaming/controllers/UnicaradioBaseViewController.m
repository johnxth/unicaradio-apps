//
//  UnicaradioBaseViewController.m
//  Streaming
//
//  Created by Paolo on 21/01/13.
//
//

#import "UnicaradioBaseViewController.h"

@interface UnicaradioBaseViewController ()

@end

@implementation UnicaradioBaseViewController

@synthesize popover;

- (void) initButtonBarItemsForNavigationItem:(UINavigationItem *)item
{
	UISegmentedControl *segmentedControl = [[UISegmentedControl alloc] initWithItems:
											[NSArray arrayWithObjects:
											 [UIImage imageNamed:@"settings"],
											 [UIImage imageNamed:@"share"],
											 nil]];
	[segmentedControl addTarget:self action:@selector(segmentAction:) forControlEvents:UIControlEventValueChanged];
	segmentedControl.frame = CGRectMake(0, 0, 90, 30);
	segmentedControl.segmentedControlStyle = UISegmentedControlStyleBar;
	segmentedControl.momentary = YES;

	UIBarButtonItem *segmentBarItem = [[UIBarButtonItem alloc] initWithCustomView:segmentedControl];

	item.rightBarButtonItem = segmentBarItem;
}

- (void) initButtonBarItems
{
	[self initButtonBarItemsForNavigationItem:self.navigationItem];
}

- (void) didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void) segmentAction:(id) sender
{
	UISegmentedControl *segmentedControl = (UISegmentedControl *) sender;
	NSInteger selectedSegment = segmentedControl.selectedSegmentIndex;

	if(selectedSegment == 0) {
		[self openSettings:sender];
	} else {
		[self openShareSheet:sender];
	}
}

- (void) openSettings:(id) sender
{
	NSLog(@"Open settings");
	if([DeviceUtils isPhone]) {
		UIViewController *settingsViewController = [SettingsViewController createSettingsController];
		[self presentModalViewController:settingsViewController animated:YES];
	} else {
		[self openSettingsForIPad:sender];
	}
}

- (void) openSettingsForIPad:(id) sender
{
	if(popover == nil) {
		UIViewController *settingsViewController = [SettingsViewController createSettingsController];
		popover = [[UIPopoverController alloc] initWithContentViewController:settingsViewController];
	}
	
	if([popover isPopoverVisible]) {
		[popover dismissPopoverAnimated:YES];
	} else {
		UISegmentedControl *control = sender;
		CGRect rect = control.frame;
		rect.origin.x -= 22;
		rect.origin.y -= 42;
		[popover presentPopoverFromRect:rect inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
	}
}

- (void) openShareSheet:(id) sender
{
	if(NSClassFromString(@"SLComposeViewController") != nil) {
		NSLog(@"ios 6!");
	} else {
		NSLog(@"ios 5!");
	}
}

@end
