//
//  Day.h
//  Streaming
//
//  Created by Paolo on 05/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Day_enum.h"

#define MONDAY_KEY @"lunedi"
#define TUESDAY_KEY @"martedi"
#define WEDNESDAY_KEY @"mercoledi"
#define THURSDAY_KEY @"giovedi"
#define FRIDAY_KEY @"venerdi"
#define SATURDAY_KEY @"sabato"
#define SUNDAY_KEY @"domenica"

@interface Day : NSObject
{
}

+ (enum Day) getDayByString: (NSString *)dayString;
+ (NSString *) getStringByDay: (enum Day)day;

@end
