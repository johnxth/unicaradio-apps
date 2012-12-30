//
//  Transmission.h
//  Streaming
//
//  Created by Paolo on 05/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

#define PROGRAM_KEY @"programma"
#define START_TIME_KEY @"inizio"

@interface Transmission  : NSObject
{
	NSString *formatName;
	NSString *startTime;
}

@property (nonatomic, strong) NSString *formatName;
@property (nonatomic, strong) NSString *startTime;

- (id) initWithFormatName: (NSString *)formatName andStartTime: (NSString *)startTime;
- (NSComparisonResult)compare: (Transmission *)otherObject;

+ (NSMutableArray *) sortTransmissions: (NSArray *)transmissionsArray;

@end
