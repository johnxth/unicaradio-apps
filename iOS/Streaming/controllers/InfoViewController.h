//
//  InfoViewController.h
//  Streaming
//
//  Created by Paolo on 27/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "UnicaradioBaseViewController.h"

@interface InfoViewController : UIViewController<UIWebViewDelegate>
{
	IBOutlet UIWebView *webView;
}

@property (nonatomic, strong) IBOutlet UIWebView *webView;

@end
