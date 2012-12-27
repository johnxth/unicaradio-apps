//
//  SongRequestViewControllerViewController.h
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../widgets/LoadingDialog.h"

#define kPlistname @"Information.plist"

@interface SongRequestViewController : UIViewController<UITextFieldDelegate>
{
	IBOutlet UIScrollView *scrollView;
    IBOutlet UIView *contentView;

	IBOutlet UITextField *emailTextView;
	IBOutlet UITextField *autoreTextView;
	IBOutlet UITextField *titoloTextView;

	LoadingDialog *dialog;

	NSOperationQueue *queue;
	NSTimer *timer;

	CGFloat animatedDistance;
}

@property (nonatomic, retain) IBOutlet UIScrollView *scrollView;
@property (nonatomic, retain) IBOutlet UIView *contentView;

@property (nonatomic, retain) IBOutlet UITextField *emailTextView;
@property (nonatomic, retain) IBOutlet UITextField *autoreTextView;
@property (nonatomic, retain) IBOutlet UITextField *titoloTextView;

- (IBAction) sendEmail:(id)sender;

@end
