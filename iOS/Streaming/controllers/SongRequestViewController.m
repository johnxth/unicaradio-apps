//
//  SongRequestViewControllerViewController.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SongRequestViewController.h"

@interface SongRequestViewController ()

@end

@implementation SongRequestViewController

@synthesize contentView;
@synthesize scrollView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"Request song", @"Request song");
        self.tabBarItem.image = [UIImage imageNamed:@"song"];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
	
	[self.scrollView addSubview: self.contentView];
    self.scrollView.contentSize = self.contentView.bounds.size;
}

- (void)viewDidUnload
{
	self.scrollView  = nil;
    self.contentView = nil;
	
    [super viewDidUnload];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

- (BOOL) textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

@end
