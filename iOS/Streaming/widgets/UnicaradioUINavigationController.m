//
//  UnicaradioUINavigationController.m
//  Streaming
//
//  Created by Paolo on 22/12/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "UnicaradioUINavigationController.h"
#import "UnicaradioUINavigationBar.h"
#import "UnicaradioUINavigationBarIos7.h"

#import "SystemUtils.h"

@interface UnicaradioUINavigationController ()

@end

@implementation UnicaradioUINavigationController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        [self setValue:[self createNavigationBar] forKeyPath:@"navigationBar"];
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

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (UINavigationBar *) createNavigationBar
{
	if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
		return [[UnicaradioUINavigationBarIos7 alloc] initWithFrame:self.navigationBar.frame];
	} else {
		return [[UnicaradioUINavigationBar alloc] initWithFrame:self.navigationBar.frame];
	}
}

@end
