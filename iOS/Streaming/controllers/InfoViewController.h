//
//  InfoViewController.h
//  Streaming
//
//  Created by Paolo on 27/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface InfoViewController : UIViewController<UIWebViewDelegate>
{
	IBOutlet UIWebView *webView;
	IBOutlet UIScrollView *scrollView;
    IBOutlet UIView *contentView;
}

@property (nonatomic, retain) IBOutlet UIScrollView *scrollView;
@property (nonatomic, retain) IBOutlet UIView *contentView;

@property (nonatomic, retain) IBOutlet UIWebView *webView;

@end
