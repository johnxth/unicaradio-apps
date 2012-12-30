//
//  DTCustomColoredAccessory.h
//  Streaming
//
//  Created by Paolo on 04/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface DTCustomColoredAccessory : UIControl
{
	UIColor *_accessoryColor;
	UIColor *_highlightedColor;
}

@property (nonatomic, strong) UIColor *accessoryColor;
@property (nonatomic, strong) UIColor *highlightedColor;

+ (DTCustomColoredAccessory *)accessoryWithColor:(UIColor *)color andHighlightedColor:(UIColor *)highlightedColor;
+ (DTCustomColoredAccessory *)accessoryWithColor:(UIColor *)color;

@end
