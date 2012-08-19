//
//  SongRequestViewControllerViewController.h
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SongRequestViewController : UIViewController<UITextFieldDelegate>
{
	IBOutlet UIScrollView *scrollView;
    IBOutlet UIView *contentView;

	IBOutlet UITextField *captchaTextView;
}

@property (nonatomic, retain) IBOutlet UIScrollView *scrollView;
@property (nonatomic, retain) IBOutlet UIView *contentView;
@property (nonatomic, retain) IBOutlet UITextField *captchaTextView;

@end
