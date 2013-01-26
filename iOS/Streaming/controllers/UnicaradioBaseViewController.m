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
@synthesize sharePopover;

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
	if([DeviceUtils isPhone]) {
		UIActionSheet *shareActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"navigation_share",@"")
																	  delegate:self
															 cancelButtonTitle:nil
														destructiveButtonTitle:nil
															 otherButtonTitles:@"Twitter", @"eMail", nil];
		int buttonsCount = 2;
		if(NSClassFromString(@"SLComposeViewController") != nil) {
			[shareActionSheet addButtonWithTitle:@"Facebook"];
			buttonsCount++;
		}
		[shareActionSheet addButtonWithTitle:@"Annulla"];
		shareActionSheet.cancelButtonIndex = buttonsCount;

		[shareActionSheet showFromTabBar:self.tabBarController.tabBar];
	} else {
		[self openShareSheetForIPad:sender];
	}
}

- (void) openShareSheetForIPad:(id) sender
{
	if(sharePopover == nil) {
		ShareViewController *shareViewController = [[ShareViewController alloc] init];
		sharePopover = [[UIPopoverController alloc] initWithContentViewController:shareViewController];
		[shareViewController setPopOver:sharePopover];
	}

	if([sharePopover isPopoverVisible]) {
		[sharePopover dismissPopoverAnimated:YES];
	} else {
		CGRect rect = CGRectMake(992, -7, 0, 0);
		sharePopover.popoverContentSize = CGSizeMake(354, 143);
		[sharePopover presentPopoverFromRect:rect inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
	}
}

- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex
{
	switch (buttonIndex) {
		case 0:
		case 1:
			[ShareViewController share:buttonIndex withUIViewController:self];
			break;
		case 2:
			if(NSClassFromString(@"SLComposeViewController") != nil) {
				[ShareViewController share:buttonIndex withUIViewController:self];
			}
			break;
		default:
			break;
	}
}

- (void)mailComposeController:(MFMailComposeViewController*)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError*)error
{
    [self dismissModalViewControllerAnimated:YES];
}

@end
