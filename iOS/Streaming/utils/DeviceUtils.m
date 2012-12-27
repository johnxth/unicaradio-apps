//
//  DeviceUtils.m
//  Streaming
//
//  Created by Paolo on 25/12/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "DeviceUtils.h"

@implementation DeviceUtils

+ (BOOL) isLandscape
{
	UIDeviceOrientation currentOrientation = [[UIApplication sharedApplication] statusBarOrientation];
	return [self isLandscape:currentOrientation];
}

+ (BOOL) isLandscape:(UIInterfaceOrientation)interfaceOrientation
{
	return UIInterfaceOrientationIsLandscape(interfaceOrientation) || interfaceOrientation == UIInterfaceOrientationLandscapeLeft || interfaceOrientation == UIInterfaceOrientationLandscapeRight;
}

+ (BOOL) isPhone
{
	return [[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone;
}

+ (BOOL) is4InchRetinaIPhone
{
	return [[UIScreen mainScreen] bounds].size.height == 568;
}

@end
