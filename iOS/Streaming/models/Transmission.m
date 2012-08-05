//
//  Transmission.m
//  Streaming
//
//  Created by Paolo on 05/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "Transmission.h"

@implementation Transmission

@synthesize formatName;
@synthesize startTime;

- (id) initWithFormatName:(NSString *)formatNameOrNil andStartTime:(NSString *)startTimeOrNil
{
	if (self = [super init])
    {
		self.formatName = formatNameOrNil;
		self.startTime = startTimeOrNil;
    }
	
    return self;
}

- (NSComparisonResult)compare: (Transmission *)otherObject {
    return [self.startTime compare:otherObject.startTime];
}

+ (NSMutableArray *) sortTransmissions: (NSArray *)transmissionsArray
{
	NSSortDescriptor *sortDescriptor;
	sortDescriptor = [[[NSSortDescriptor alloc] initWithKey:@"startTime"
												  ascending:YES] autorelease];
	NSArray *sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
	NSMutableArray *sortedArray = [[transmissionsArray sortedArrayUsingDescriptors:sortDescriptors] mutableCopy];
	
	return sortedArray;
}

@end
