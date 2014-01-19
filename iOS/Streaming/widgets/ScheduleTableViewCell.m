//
//  ScheduleTableViewCell.m
//  Streaming
//
//  Created by Paolo Cortis on 19/01/14.
//
//

#import "ScheduleTableViewCell.h"

@implementation ScheduleTableViewCell

@synthesize textLabel;
@synthesize detailTextLabel;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
