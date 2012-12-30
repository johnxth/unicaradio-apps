//
//  LoadingDialog.m
//  Streaming
//
//  Created by Paolo on 20/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "LoadingDialog.h"

@implementation LoadingDialog

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if(self) {
        // Initialization code
    }
    return self;
}

- (void)drawRect:(CGRect)rect
{
	[super drawRect:rect];

    UIActivityIndicatorView *activityIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    activityIndicatorView.frame = CGRectMake(121.0f, 50.0f, 37.0f, 37.0f);

    [self addSubview:activityIndicatorView];
    [activityIndicatorView startAnimating];
}

- (void) dismiss
{
	[self dismissWithClickedButtonIndex:0 animated:YES];
}

@end
