//
//  DeviceUtils.h
//  Streaming
//
//  Created by Paolo on 25/12/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DeviceUtils : NSObject
{
}

+ (BOOL) isLandscape;

+ (BOOL) isLandscape:(UIInterfaceOrientation)interfaceOrientation;

+ (BOOL) isPhone;

+ (BOOL) is4InchRetinaIPhone;

@end
