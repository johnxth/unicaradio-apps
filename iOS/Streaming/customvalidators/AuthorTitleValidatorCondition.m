//
//  AuthorTitleValidatorCondition.m
//  Streaming
//
//  Created by Paolo on 19/01/13.
//
//

#import "AuthorTitleValidatorCondition.h"
#import "US2ConditionRange.h"

@implementation AuthorTitleValidatorCondition

- (id)init
{
    self = [super init];
    if (self)
    {
        US2ConditionRange *rangeCondition   = [[US2ConditionRange alloc] init];
        rangeCondition.range                = NSMakeRange(2, 30);
        rangeCondition.shouldAllowViolation = YES;
        
        [self addCondition:rangeCondition];
    }
    
    return self;
}

@end
