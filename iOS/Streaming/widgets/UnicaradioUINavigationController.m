//
//  UnicaradioUINavigationController.m
//  Streaming
//
//  Created by Paolo on 22/12/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "UnicaradioUINavigationController.h"
#import "UnicaradioUINavigationBar.h"

#import "SystemUtils.h"

@interface UnicaradioUINavigationController ()

@end

@implementation UnicaradioUINavigationController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        [self setValue:[[UnicaradioUINavigationBar alloc] initWithFrame:self.navigationBar.frame] forKeyPath:@"navigationBar"];
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

@end
