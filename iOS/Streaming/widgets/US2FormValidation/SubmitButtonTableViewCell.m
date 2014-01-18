//
//  SubmitButtonTableViewCell.m
//  US2FormValidationFramework
//
//  Copyright (C) 2012 ustwo™
//  
//  Permission is hereby granted, free of charge, to any person obtaining a copy of
//  this software and associated documentation files (the "Software"), to deal in
//  the Software without restriction, including without limitation the rights to
//  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
//  of the Software, and to permit persons to whom the Software is furnished to do
//  so, subject to the following conditions:
//  
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//  
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.
//  

#import "SubmitButtonTableViewCell.h"
#import "DeviceUtils.h"
#import "SystemUtils.h"

@interface SubmitButtonTableViewCell ()
	- (void)_initUserInterface;
@end

@implementation SubmitButtonTableViewCell

@synthesize button = _button;

- (id)initWithReuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentifier];
    if (self)
    {
        [self _initUserInterface];
    }
    
    return self;
}

#pragma mark - Build user interface

- (void)_initUserInterface
{
	[self _initUserInterface:self.bounds];
}

- (void)_initUserInterface:(CGRect)frame
{    
    // Add button
	float x = 0.0; // iPhone portrait
	float width = 300.0;
	if(![DeviceUtils isPhone]) {
		x = 300.0; //iPad
		if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
			width = 250.0;
			x = 380.0;
		}
	} else if([DeviceUtils isLandscape]) {
		if([DeviceUtils is4InchRetinaIPhone]) {
			width = 550.0;
		} else {
			width = 460.0;
		}
		if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
			x = 10.0;
		}
	} else {
		if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
			x = 10.0;
		}
	}

	UIColor *background = [UIColor clearColor];
	if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
		background = [UIColor whiteColor];
	}

    CGRect buttonFrame = CGRectMake(x, 0.0, width, frame.size.height);
    _button = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    _button.frame = buttonFrame;
	_button.backgroundColor = background;
    [_button setTitle:NSLocalizedString(@"SUBMIT_BUTTON", @"") forState:UIControlStateNormal];
    [self.contentView addSubview:_button];

    // Set selection style of cell
    self.selectionStyle = UITableViewCellSelectionStyleNone;

    // Remove background
    self.backgroundView = [[UIView alloc] init];
}

#pragma mark - Selection

/**
 Do not show any selection animation, so no super call here.
*/
- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    //
}

/**
 Do not show any highlight animation, so no super call here.
*/
- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated
{
    //
}


@end
