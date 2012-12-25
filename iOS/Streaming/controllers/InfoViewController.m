//
//  InfoViewController.m
//  Streaming
//
//  Created by Paolo on 27/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "InfoViewController.h"

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
        self.title = NSLocalizedString(@"Info", @"Info");
        self.tabBarItem.image = [UIImage imageNamed:@"info"];
    }
    NSLog(@"init info view controller");
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
	
	NSString *htmlString = @"<html><head><style type='text/css'>body { color: #FFFFFF; } b { color: #FF0000; } a { color: #00FF00; } </style></head><body><b>UnicaRadio</b> la webradio degli studenti universitari di Cagliari.<br /><br />Software sviluppato da <a href='http://code.google.com/p/unicaradio-apps/wiki/Developer'>Paolo Cortis</a> per conto di <b>UnicaRadio</b>, sotto i termini e le condizioni della licenza GPLv2.<br /><br />Codice sorgente liberamente scaricabile da <a href='http://code.google.com/p/unicaradio-apps/'>Google Code</a>.</body></html>";

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
