//
//  NoItemSelectedViewController.m
//  Streaming
//
//  Created by Paolo on 24/12/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "NoItemSelectedViewController.h"

@interface NoItemSelectedViewController ()

@end

@implementation NoItemSelectedViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
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
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)dealloc {
	[super dealloc];
}
@end
