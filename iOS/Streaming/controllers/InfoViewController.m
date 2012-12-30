//
//  InfoViewController.m
//  Streaming
//
//  Created by Paolo on 27/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "InfoViewController.h"
#import "../utils/DeviceUtils.h"

@interface InfoViewController ()

@end

@implementation InfoViewController

@synthesize webView;
@synthesize scrollView;
@synthesize contentView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"CONTROLLER_TITLE_INFO", @"");
        self.tabBarItem.image = [UIImage imageNamed:@"info"];
    }
    NSLog(@"init info view controller");
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void) viewWillAppear:(BOOL)animated
{
	NSString *filePath = [[NSBundle mainBundle] pathForResource:@"app_infos" ofType:@"htm"];
	NSError *error = nil;
	NSString *htmlString = [NSString stringWithContentsOfFile:filePath encoding:NSUTF8StringEncoding error:&error];

	[self.scrollView addSubview: self.contentView];
    self.scrollView.contentSize = self.contentView.bounds.size;

	//make the background transparent
    [webView setBackgroundColor:[UIColor clearColor]];
	[[[webView subviews] lastObject] setScrollEnabled:NO];
	[webView loadHTMLString:htmlString baseURL:nil];
}

- (void)viewDidUnload
{
	self.scrollView  = nil;
    self.contentView = nil;
	
    [super viewDidUnload];
}

-(BOOL) webView:(UIWebView *)inWeb shouldStartLoadWithRequest:(NSURLRequest *)inRequest navigationType:(UIWebViewNavigationType)inType
{
    if(inType == UIWebViewNavigationTypeLinkClicked) {
        [[UIApplication sharedApplication] openURL:[inRequest URL]];
        return NO;
    }

    return YES;
}

@end
