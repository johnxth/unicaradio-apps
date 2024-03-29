//
//  UnicaradioBaseViewController.m
//  Streaming
//
//  Created by Paolo on 21/01/13.
//
//

#import <objc/runtime.h>

#import "UnicaradioBaseViewController.h"
#import "SystemUtils.h"

static char *const popoverKey = "popoverKey";
static char *const sharePopoverKey = "sharePopoverKey";

@implementation UIViewController (UnicaradioButtons)

- (void)setPopover:(UIPopoverController *)popover
{
	objc_setAssociatedObject(self, popoverKey, popover, OBJC_ASSOCIATION_RETAIN);
}

- (UIPopoverController *)popover
{
	return objc_getAssociatedObject(self, popoverKey);
}

- (void)setSharePopover:(UIPopoverController *)sharePopover
{
	objc_setAssociatedObject(self, sharePopoverKey, sharePopover, OBJC_ASSOCIATION_RETAIN);
}

- (UIPopoverController *)sharePopover
{
	return objc_getAssociatedObject(self, sharePopoverKey);
}

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
		[self presentViewController:settingsViewController animated:YES completion:nil];
	} else {
		[self openSettingsForIPad:sender];
	}
}

- (void) openSettingsForIPad:(id) sender
{
	if(self.popover == nil) {
		UIViewController *settingsViewController = [SettingsViewController createSettingsController];
		self.popover = [[UIPopoverController alloc] initWithContentViewController:settingsViewController];
	}

	if([self.popover isPopoverVisible]) {
		[self.popover dismissPopoverAnimated:YES];
	} else {
		UISegmentedControl *control = sender;
		CGRect rect = [self getRectForSettingsPopover:control];

		[self.popover presentPopoverFromRect:rect inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
	}
}

- (CGRect) getRectForSettingsPopover:(UISegmentedControl *)control
{
	CGRect rect = control.frame;
	if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
		rect.origin.x -= 22;
		rect.origin.y += 20;
	} else {
		rect.origin.x -= 22;
		rect.origin.y -= 42;
	}

	return rect;
}

- (void) openShareSheet:(id) sender
{
	if([DeviceUtils isPhone]) {
		UIActionSheet *shareActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"SHARE_SHEET_TITLE",@"")
																	  delegate:self
															 cancelButtonTitle:nil
														destructiveButtonTitle:nil
															 otherButtonTitles:@"Twitter", @"eMail", nil];
		int buttonsCount = 2;
		if(NSClassFromString(@"SLComposeViewController") != nil) {
			[shareActionSheet addButtonWithTitle:@"Facebook"];
			buttonsCount++;
		}
		[shareActionSheet addButtonWithTitle:NSLocalizedString(@"CANCEL_BUTTON", @"")];
		shareActionSheet.cancelButtonIndex = buttonsCount;

		[shareActionSheet showFromTabBar:self.tabBarController.tabBar];
	} else {
		[self openShareSheetForIPad:sender];
	}
}

- (void) openShareSheetForIPad:(id) sender
{
	if(self.sharePopover == nil) {
		ShareViewController *shareViewController = [[ShareViewController alloc] init];
		shareViewController.delegate = self;
		UINavigationController *navShareViewController = [[UINavigationController alloc] initWithRootViewController:shareViewController];
		self.sharePopover = [[UIPopoverController alloc] initWithContentViewController:navShareViewController];
		[shareViewController setPopOver:self.sharePopover];
	}

	if([self.sharePopover isPopoverVisible]) {
		[self.sharePopover dismissPopoverAnimated:YES];
	} else {
		UISegmentedControl *control = sender;
		CGRect rect = [self getRectForSharePopover:control];
		self.sharePopover.popoverContentSize = CGSizeMake(354, 175);
		[self.sharePopover presentPopoverFromRect:rect inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
	}
}


- (void) dismissSharePopoverAnimated:(BOOL)animated
{
	[self.sharePopover dismissPopoverAnimated:animated];
}

- (void) presentViewControllerForSharePopover:(UIViewController *)viewController
{
	[self presentViewController:viewController animated:YES completion:nil];
}

- (CGRect) getRectForSharePopover:(UISegmentedControl *)control
{
	CGRect rect = control.frame;
	if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
		rect.origin.x += 22;
		rect.origin.y += 20;
	} else {
		rect.origin.x += 32;
		rect.origin.y -= 42;
	}
	
	return rect;
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
	if(buttonIndex > FACEBOOK) {
		return;
	}
	
	UIViewController *controller;
	controller = [ShareViewController share:buttonIndex withUIViewController:self];
	[self presentViewController:controller animated:YES completion:nil];
}

- (void)mailComposeController:(MFMailComposeViewController*)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError*)error
{
    [self dismissModalViewControllerAnimated:YES];
}

@end
