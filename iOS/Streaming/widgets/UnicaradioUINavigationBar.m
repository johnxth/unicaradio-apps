//
//  UnicaradioUINavigationBar.m
//  Streaming
//
//  Created by Paolo on 02/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "UnicaradioUINavigationBar.h"
#import <QuartzCore/QuartzCore.h>

@implementation UnicaradioUINavigationBar

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)drawRect:(CGRect)rect
{
    UIColor *startRed = [UIColor colorWithRed:0xA8/255.0 green:0 blue:0 alpha:1];
    UIColor *middleRed = [UIColor colorWithRed:0x7F/255.0 green:0 blue:0 alpha:1];
    UIColor *endRed = [UIColor colorWithRed:0x69/255.0 green:0 blue:0 alpha:1];

	CAGradientLayer *gradientNavBar = [CAGradientLayer layer];
	gradientNavBar.colors = [NSArray arrayWithObjects:(id)[startRed CGColor], (id)[middleRed CGColor], (id)[endRed CGColor], nil];
	gradientNavBar.frame = self.bounds;
	gradientNavBar.name = @"unicaradioUINavigationBar";
	[self.layer insertSublayer:gradientNavBar atIndex:0];  
	self.tintColor = startRed;
}


@end
