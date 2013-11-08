//
//  UnicaradioUINavigationBar.m
//  Streaming
//
//  Created by Paolo on 02/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "UnicaradioUINavigationBar.h"
#import <QuartzCore/QuartzCore.h>

#define LIGHT_COLOR_COMPONENTS     { 0.659, 0.000, 0.000, 1.0, 0.498, 0.000, 0.000, 1.0 }
#define MAIN_COLOR_COMPONENTS      { 0.498, 0.000, 0.000, 1.0, 0.412, 0.000, 0.000, 1.0 }

#define SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedAscending)

@implementation UnicaradioUINavigationBar

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
		if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
			UIColor *startRed = [UIColor colorWithRed:0xA8/255.0 green:0 blue:0 alpha:1];
			self.barStyle = UIBarStyleBlackOpaque;
			self.barTintColor = startRed;
			self.tintColor = [UIColor whiteColor];
			self.backgroundColor = startRed;
		}
    }
    return self;
}

- (void) drawRect:(CGRect)rect
{
	if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
		//[super drawRect:rect];
		return;
	}

	UIColor *startRed = [UIColor colorWithRed:0xA8/255.0 green:0 blue:0 alpha:1];

	self.tintColor = startRed;
	CGContextRef context = UIGraphicsGetCurrentContext();
	CGFloat locations[2] = { 0.0, 1.0 };
	CGColorSpaceRef myColorspace = CGColorSpaceCreateDeviceRGB();
	
	CGFloat topComponents[8] = LIGHT_COLOR_COMPONENTS;
	CGGradientRef topGradient = CGGradientCreateWithColorComponents(myColorspace, topComponents, locations, 2);
	CGContextDrawLinearGradient(context, topGradient, CGPointMake(0, 0), CGPointMake(0,self.frame.size.height/2), 0);
	CGGradientRelease(topGradient);
	
	CGFloat botComponents[8] = MAIN_COLOR_COMPONENTS;
	CGGradientRef botGradient = CGGradientCreateWithColorComponents(myColorspace, botComponents, locations, 2);
	CGContextDrawLinearGradient(context, botGradient,
								CGPointMake(0,self.frame.size.height/2), CGPointMake(0, self.frame.size.height), 0);
	CGGradientRelease(botGradient);
	
	CGColorSpaceRelease(myColorspace);
	
	
	// top Line
	CGContextSetRGBStrokeColor(context, 1, 1, 1, 1.0);
	CGContextMoveToPoint(context, 0, 0);
	CGContextAddLineToPoint(context, self.frame.size.width, 0);
	CGContextStrokePath(context);
	
	// bottom line
	CGContextSetRGBStrokeColor(context, 0, 0, 0, 1.0);
	CGContextMoveToPoint(context, 0, self.frame.size.height);
	CGContextAddLineToPoint(context, self.frame.size.width, self.frame.size.height);
	CGContextStrokePath(context);
}

@end
