//
//  UnicaradioUITabBarIos7.m
//  Streaming
//
//  Created by Paolo Cortis on 09/11/13.
//
//

#import "UnicaradioUITabBarIos7.h"

@implementation UnicaradioUITabBarIos7

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
		UIColor *startRed = [UIColor colorWithRed:0xA8/255.0 green:0 blue:0 alpha:1];
		self.barStyle = UIBarStyleBlackOpaque;
		self.barTintColor = startRed;
		self.tintColor = [UIColor whiteColor];
		self.backgroundColor = startRed;
    }
    return self;
}

@end
